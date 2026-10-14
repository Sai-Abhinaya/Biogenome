/**
 * BioGenome – Universal Simulation State Engine
 * Manages discrete state playback, stepping, speed, progress, and callbacks.
 * 100% Client-Side – Zero Backend Dependencies.
 */

class SimulationEngine {
  constructor(options = {}) {
    this.states = [];
    this.currentIndex = 0;
    this.isPlaying = false;
    this.timer = null;
    this.speed = options.speed || 'normal'; // 'slow', 'normal', 'fast'
    this.speedDelays = {
      slow: 800,
      normal: 350,
      fast: 100
    };
    this.onStateChange = options.onStateChange || null;
    this.onComplete = options.onComplete || null;

    // DOM UI Bindings (optional auto-wire)
    this.ui = {
      prevBtn: options.prevBtn || null,
      nextBtn: options.nextBtn || null,
      playBtn: options.playBtn || null,
      resetBtn: options.resetBtn || null,
      endBtn: options.endBtn || null,
      speedSelect: options.speedSelect || null,
      counterEl: options.counterEl || null,
      progressFill: options.progressFill || null,
      operationEl: options.operationEl || null,
      explanationEl: options.explanationEl || null,
      mathEl: options.mathEl || null
    };

    if (options.bindControls) {
      this.attachControls(this.ui);
    }
  }

  loadStates(states) {
    this.pause();
    this.states = Array.isArray(states) && states.length > 0 ? states : [{
      explanation: 'No simulation steps generated.',
      operation: 'Idle',
      math: ''
    }];
    this.currentIndex = 0;
    this.updateUI();
  }

  getCurrentState() {
    return this.states[this.currentIndex] || null;
  }

  stepNext() {
    if (this.currentIndex < this.states.length - 1) {
      this.currentIndex++;
      this.updateUI();
      return true;
    } else {
      this.pause();
      if (typeof this.onComplete === 'function') {
        this.onComplete(this.getCurrentState());
      }
      return false;
    }
  }

  stepPrev() {
    if (this.currentIndex > 0) {
      this.currentIndex--;
      this.updateUI();
      return true;
    }
    return false;
  }

  goToStep(index) {
    if (index >= 0 && index < this.states.length) {
      this.currentIndex = index;
      this.updateUI();
    }
  }

  play() {
    if (this.isPlaying) return;
    if (this.currentIndex >= this.states.length - 1) {
      this.currentIndex = 0;
    }
    this.isPlaying = true;
    this.updateControlsState();
    
    const delay = this.speedDelays[this.speed] || 350;
    this.timer = setInterval(() => {
      const advanced = this.stepNext();
      if (!advanced) {
        this.pause();
      }
    }, delay);
  }

  pause() {
    this.isPlaying = false;
    if (this.timer) {
      clearInterval(this.timer);
      this.timer = null;
    }
    this.updateControlsState();
  }

  reset() {
    this.pause();
    this.currentIndex = 0;
    this.updateUI();
  }

  runToEnd() {
    this.pause();
    this.currentIndex = Math.max(0, this.states.length - 1);
    this.updateUI();
    if (typeof this.onComplete === 'function') {
      this.onComplete(this.getCurrentState());
    }
  }

  setSpeed(speed) {
    if (this.speedDelays[speed]) {
      this.speed = speed;
      if (this.isPlaying) {
        this.pause();
        this.play();
      }
    }
  }

  updateUI() {
    const currentState = this.getCurrentState();
    const total = this.states.length;
    const current = this.currentIndex + 1;
    const pct = total > 1 ? ((this.currentIndex / (total - 1)) * 100) : (total === 1 ? 100 : 0);

    // Progress counter
    if (this.ui.counterEl) {
      this.ui.counterEl.textContent = `Step ${current} / ${total}`;
    }
    // Progress fill bar
    if (this.ui.progressFill) {
      this.ui.progressFill.style.width = `${pct}%`;
    }
    // Operation label
    if (this.ui.operationEl && currentState) {
      this.ui.operationEl.textContent = currentState.operation || `Processing step ${current}`;
    }
    // Explanation body
    if (this.ui.explanationEl && currentState) {
      this.ui.explanationEl.innerHTML = currentState.explanation || 'Executing algorithmic transformation...';
    }
    // Math / State detail
    if (this.ui.mathEl) {
      if (currentState && currentState.math) {
        this.ui.mathEl.style.display = 'block';
        this.ui.mathEl.textContent = currentState.math;
      } else {
        this.ui.mathEl.style.display = 'none';
      }
    }

    this.updateControlsState();

    if (typeof this.onStateChange === 'function' && currentState) {
      this.onStateChange(currentState, this.currentIndex, total);
    }
  }

  updateControlsState() {
    if (this.ui.playBtn) {
      this.ui.playBtn.innerHTML = this.isPlaying 
        ? '<span class="btn-icon">⏸</span> Pause' 
        : '<span class="btn-icon">▶</span> Play';
      this.ui.playBtn.setAttribute('title', this.isPlaying ? 'Pause simulation' : 'Play simulation');
    }
    if (this.ui.prevBtn) {
      this.ui.prevBtn.disabled = this.currentIndex <= 0;
    }
    if (this.ui.nextBtn) {
      this.ui.nextBtn.disabled = this.currentIndex >= this.states.length - 1;
    }
  }

  attachControls(elements) {
    this.ui = { ...this.ui, ...elements };

    if (this.ui.prevBtn) {
      this.ui.prevBtn.onclick = () => { this.pause(); this.stepPrev(); };
    }
    if (this.ui.nextBtn) {
      this.ui.nextBtn.onclick = () => { this.pause(); this.stepNext(); };
    }
    if (this.ui.playBtn) {
      this.ui.playBtn.onclick = () => {
        if (this.isPlaying) this.pause();
        else this.play();
      };
    }
    if (this.ui.resetBtn) {
      this.ui.resetBtn.onclick = () => this.reset();
    }
    if (this.ui.endBtn) {
      this.ui.endBtn.onclick = () => this.runToEnd();
    }
    if (this.ui.speedSelect) {
      this.ui.speedSelect.onchange = (e) => this.setSpeed(e.target.value);
    }
  }
}

window.SimulationEngine = SimulationEngine;
