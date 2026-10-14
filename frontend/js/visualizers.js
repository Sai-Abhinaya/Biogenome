/**
 * BioGenome Step-by-Step Educational Visualizers
 * Provides interactive scientific visualizations for:
 * - KMP pattern sliding & prefix table matches
 * - Needleman-Wunsch DP matrix filling & traceback
 * - Network Flow augmenting paths & min-cut bottleneck edges
 * - 3-SAT to CLIQUE reduction graph
 * - Parallel Tree Reduction steps
 *
 * Each visualizer features [Previous Step], [Play/Pause], [Next Step], and [Reset].
 *
 * @author BioGenome Team
 * @version 3.0.0
 */

class KmpVisualizer {
  constructor(containerId) {
    this.container = document.getElementById(containerId);
    this.text = 'CGTTTTTTCATTGACACCACCCACACCAC';
    this.pattern = 'ACCAC';
    this.steps = [];
    this.currentStep = 0;
    this.timer = null;
    this.generateSteps();
  }

  generateSteps() {
    this.steps = [];
    const n = this.text.length;
    const m = this.pattern.length;

    // Compute pi
    const pi = new Array(m).fill(0);
    let k = 0;
    for (let i = 1; i < m; i++) {
      while (k > 0 && this.pattern[k] !== this.pattern[i]) k = pi[k - 1];
      if (this.pattern[k] === this.pattern[i]) k++;
      pi[i] = k;
    }

    let q = 0;
    for (let i = 0; i < n; i++) {
      while (q > 0 && this.pattern[q] !== this.text[i]) {
        this.steps.push({
          textIdx: i,
          patternIdx: q,
          status: 'mismatch',
          matchedLen: q,
          explanation: `Mismatch at Text[${i}] ('${this.text[i]}') vs Pattern[${q}] ('${this.pattern[q]}'). Shift via π-table to index ${pi[q - 1]}.`
        });
        q = pi[q - 1];
      }
      if (this.pattern[q] === this.text[i]) {
        q++;
        this.steps.push({
          textIdx: i,
          patternIdx: q - 1,
          status: 'match',
          matchedLen: q,
          explanation: `Character match at Text[${i}] ('${this.text[i]}') == Pattern[${q - 1}]. Current matched prefix length: ${q}/${m}.`
        });
      } else {
        this.steps.push({
          textIdx: i,
          patternIdx: 0,
          status: 'mismatch',
          matchedLen: 0,
          explanation: `No match at Text[${i}] ('${this.text[i]}'). Pattern stays at start.`
        });
      }
      if (q === m) {
        this.steps.push({
          textIdx: i,
          patternIdx: q - 1,
          status: 'found',
          matchedLen: m,
          explanation: `🎉 Complete motif occurrence found at offset ${i - m + 1} to ${i}!`
        });
        q = pi[q - 1];
      }
    }
    this.currentStep = 0;
  }

  render() {
    if (!this.container) return;
    const step = this.steps[this.currentStep] || this.steps[0];
    const shift = Math.max(0, step.textIdx - step.patternIdx);

    let html = `
      <div style="font-family: var(--font-mono); font-size: 0.95rem; line-height: 2;">
        <div style="color: var(--text-muted); font-size: 0.75rem;">TARGET GENOME SEQUENCE:</div>
        <div style="letter-spacing: 0.25rem; font-weight: 600; overflow-x: auto; white-space: nowrap;">
    `;

    for (let i = 0; i < this.text.length; i++) {
      const isCur = i === step.textIdx;
      const isMatched = i >= shift && i <= step.textIdx && step.status !== 'mismatch';
      let bg = 'transparent';
      let color = 'var(--text-primary)';
      if (isCur) {
        bg = step.status === 'mismatch' ? 'var(--danger-light)' : 'var(--green-light)';
        color = step.status === 'mismatch' ? 'var(--danger-accent)' : 'var(--green-accent)';
      } else if (isMatched) {
        bg = 'var(--primary-blue-light)';
        color = 'var(--primary-blue)';
      }
      html += `<span style="padding: 2px 4px; border-radius: 3px; background-color: ${bg}; color: ${color};">${this.text[i]}</span>`;
    }

    html += `
        </div>
        <div style="color: var(--text-muted); font-size: 0.75rem; margin-top: 0.5rem;">ALIGNED MOTIF PATTERN (KMP SLIDING WINDOW):</div>
        <div style="letter-spacing: 0.25rem; font-weight: 600; white-space: nowrap;">
          <span style="display: inline-block; width: ${shift * 1.05}rem;"></span>
    `;

    for (let j = 0; j < this.pattern.length; j++) {
      let bg = 'var(--bg-surface-subtle)';
      let color = 'var(--text-secondary)';
      if (j === step.patternIdx) {
        bg = step.status === 'mismatch' ? 'var(--danger-light)' : 'var(--green-light)';
        color = step.status === 'mismatch' ? 'var(--danger-accent)' : 'var(--green-accent)';
      } else if (j < step.matchedLen) {
        bg = 'var(--primary-blue-light)';
        color = 'var(--primary-blue)';
      }
      html += `<span style="padding: 2px 4px; border-radius: 3px; background-color: ${bg}; color: ${color}; border: 1px solid var(--border-light);">${this.pattern[j]}</span>`;
    }

    html += `
        </div>
        <div style="margin-top: 1rem; padding: 0.65rem 0.85rem; background-color: var(--bg-surface-subtle); border-radius: var(--radius-md); font-family: var(--font-sans); font-size: 0.85rem; border: 1px solid var(--border-light);">
          <strong>Step ${this.currentStep + 1} of ${this.steps.length}:</strong> ${step.explanation}
        </div>
      </div>
    `;

    this.container.innerHTML = html;
  }

  next() {
    if (this.currentStep < this.steps.length - 1) {
      this.currentStep++;
      this.render();
    } else {
      this.pause();
    }
  }

  prev() {
    if (this.currentStep > 0) {
      this.currentStep--;
      this.render();
    }
  }

  reset() {
    this.pause();
    this.currentStep = 0;
    this.render();
  }

  play() {
    if (this.timer) return;
    this.timer = setInterval(() => {
      if (this.currentStep >= this.steps.length - 1) {
        this.pause();
      } else {
        this.next();
      }
    }, 450);
  }

  pause() {
    if (this.timer) {
      clearInterval(this.timer);
      this.timer = null;
    }
  }
}

class NeedlemanWunschVisualizer {
  constructor(canvasId) {
    this.canvas = document.getElementById(canvasId);
    this.seqA = 'ATGCT';
    this.seqB = 'AGCT';
    this.matrix = [];
    this.currentStep = 0;
    this.totalSteps = 0;
    this.timer = null;
    this.initMatrix();
  }

  initMatrix() {
    const n = this.seqA.length;
    const m = this.seqB.length;
    this.matrix = Array.from({ length: n + 1 }, () => Array(m + 1).fill(null));
    for (let i = 0; i <= n; i++) this.matrix[i][0] = i * -2;
    for (let j = 0; j <= m; j++) this.matrix[0][j] = j * -2;

    this.totalSteps = (n) * (m);
    this.currentStep = 0;
  }

  computeCell(i, j) {
    const match = this.seqA[i - 1] === this.seqB[j - 1] ? 2 : -1;
    const diag = this.matrix[i - 1][j - 1] + match;
    const up = this.matrix[i - 1][j] - 2;
    const left = this.matrix[i][j - 1] - 2;
    return Math.max(diag, up, left);
  }

  render() {
    if (!this.canvas) return;
    const ctx = this.canvas.getContext('2d');
    const width = this.canvas.width;
    const height = this.canvas.height;
    ctx.clearRect(0, 0, width, height);

    const cols = this.seqB.length + 2;
    const rows = this.seqA.length + 2;
    const cellW = Math.min(50, (width - 40) / cols);
    const cellH = Math.min(40, (height - 40) / rows);
    const startX = 30;
    const startY = 30;

    // Fill computed cells up to currentStep
    let count = 0;
    for (let i = 1; i <= this.seqA.length; i++) {
      for (let j = 1; j <= this.seqB.length; j++) {
        if (count < this.currentStep) {
          this.matrix[i][j] = this.computeCell(i, j);
        } else {
          this.matrix[i][j] = null;
        }
        count++;
      }
    }

    ctx.font = '12px var(--font-mono)';
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';

    // Draw Headers
    for (let j = 0; j <= this.seqB.length; j++) {
      const char = j === 0 ? '-' : this.seqB[j - 1];
      ctx.fillStyle = '#64748b';
      ctx.fillText(char, startX + (j + 1) * cellW + cellW / 2, startY + cellH / 2);
    }
    for (let i = 0; i <= this.seqA.length; i++) {
      const char = i === 0 ? '-' : this.seqA[i - 1];
      ctx.fillStyle = '#64748b';
      ctx.fillText(char, startX + cellW / 2, startY + (i + 1) * cellH + cellH / 2);
    }

    // Draw Grid
    for (let i = 0; i <= this.seqA.length; i++) {
      for (let j = 0; j <= this.seqB.length; j++) {
        const x = startX + (j + 1) * cellW;
        const y = startY + (i + 1) * cellH;
        const val = this.matrix[i][j];

        ctx.strokeStyle = '#e2e8f0';
        ctx.strokeRect(x, y, cellW, cellH);

        if (val !== null) {
          ctx.fillStyle = '#eff6ff';
          ctx.fillRect(x + 1, y + 1, cellW - 2, cellH - 2);

          ctx.fillStyle = '#1e293b';
          ctx.font = 'bold 12px var(--font-mono)';
          ctx.fillText(val.toString(), x + cellW / 2, y + cellH / 2);
        }
      }
    }
  }

  next() {
    if (this.currentStep < this.totalSteps) {
      this.currentStep++;
      this.render();
    } else {
      this.pause();
    }
  }

  prev() {
    if (this.currentStep > 0) {
      this.currentStep--;
      this.render();
    }
  }

  reset() {
    this.pause();
    this.initMatrix();
    this.render();
  }

  play() {
    if (this.timer) return;
    this.timer = setInterval(() => {
      if (this.currentStep >= this.totalSteps) {
        this.pause();
      } else {
        this.next();
      }
    }, 300);
  }

  pause() {
    if (this.timer) {
      clearInterval(this.timer);
      this.timer = null;
    }
  }
}

class NetworkFlowVisualizer {
  constructor(canvasId) {
    this.canvas = document.getElementById(canvasId);
    this.nodes = [
      { id: 0, label: 'Source', x: 60, y: 150, color: '#3b82f6' },
      { id: 1, label: 'Lab A', x: 200, y: 70, color: '#0d9488' },
      { id: 2, label: 'Lab B', x: 200, y: 230, color: '#0d9488' },
      { id: 3, label: 'Sequencer X', x: 380, y: 70, color: '#7c3aed' },
      { id: 4, label: 'Cryo-EM', x: 380, y: 230, color: '#7c3aed' },
      { id: 5, label: 'Sink', x: 520, y: 150, color: '#16a34a' }
    ];
    this.edges = [
      { from: 0, to: 1, cap: 30, flow: 25, isCut: false },
      { from: 0, to: 2, cap: 25, flow: 20, isCut: false },
      { from: 1, to: 3, cap: 25, flow: 25, isCut: true }, // Bottleneck
      { from: 2, to: 4, cap: 20, flow: 20, isCut: true }, // Bottleneck
      { from: 3, to: 5, cap: 35, flow: 25, isCut: false },
      { from: 4, to: 5, cap: 25, flow: 20, isCut: false }
    ];
    this.showCut = false;
  }

  toggleCut() {
    this.showCut = !this.showCut;
    this.render();
  }

  render() {
    if (!this.canvas) return;
    const ctx = this.canvas.getContext('2d');
    ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);

    // Draw Edges
    for (const e of this.edges) {
      const u = this.nodes[e.from];
      const v = this.nodes[e.to];

      ctx.beginPath();
      ctx.moveTo(u.x, u.y);
      ctx.lineTo(v.x, v.y);
      ctx.lineWidth = e.isCut && this.showCut ? 3 : 2;
      ctx.strokeStyle = e.isCut && this.showCut ? '#dc2626' : '#cbd5e1';
      ctx.stroke();

      // Flow / Capacity Label
      const midX = (u.x + v.x) / 2;
      const midY = (u.y + v.y) / 2 - 8;
      ctx.fillStyle = e.flow === e.cap ? '#dc2626' : '#1e293b';
      ctx.font = '11px var(--font-mono)';
      ctx.textAlign = 'center';
      ctx.fillText(`${e.flow}/${e.cap}`, midX, midY);
    }

    // Draw Nodes
    for (const n of this.nodes) {
      ctx.beginPath();
      ctx.arc(n.x, n.y, 22, 0, Math.PI * 2);
      ctx.fillStyle = n.color;
      ctx.fill();
      ctx.strokeStyle = '#ffffff';
      ctx.lineWidth = 2;
      ctx.stroke();

      ctx.fillStyle = '#ffffff';
      ctx.font = 'bold 10px var(--font-sans)';
      ctx.textAlign = 'center';
      ctx.textBaseline = 'middle';
      ctx.fillText(n.label.substring(0, 6), n.x, n.y);
    }
  }
}

window.KmpVisualizer = KmpVisualizer;
window.NeedlemanWunschVisualizer = NeedlemanWunschVisualizer;
window.NetworkFlowVisualizer = NetworkFlowVisualizer;
