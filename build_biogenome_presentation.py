import os
import math
from pptx import Presentation
from pptx.util import Inches, Pt
from pptx.dml.color import RGBColor
from pptx.enum.text import PP_ALIGN, MSO_ANCHOR
from pptx.enum.shapes import MSO_SHAPE

# ==============================================================================
# COLOR PALETTE (Academic Scientific Theme)
# ==============================================================================
NAVY_DARK    = RGBColor(11, 25, 44)       # Deep Navy #0B192C (Hero / Top Bar)
NAVY_MEDIUM  = RGBColor(22, 43, 74)       # Mid Navy #162B4A
TEAL         = RGBColor(10, 147, 150)     # Primary Accent Teal #0A9396
TEAL_LIGHT   = RGBColor(230, 246, 246)    # Light Teal tint for badges
TEAL_BORDER  = RGBColor(148, 210, 212)    # Soft Teal border
MINT_GREEN   = RGBColor(45, 106, 79)      # Muted Deep Green #2D6A4F
MINT_LIGHT   = RGBColor(235, 246, 240)    # Soft Mint background
GREEN_ACCENT = RGBColor(82, 183, 136)     # Vibrant Sage/Mint #52B788
BG_LIGHT     = RGBColor(245, 247, 250)    # Off-White / Slate 50 background
WHITE        = RGBColor(255, 255, 255)    # Pure White
TEXT_DARK    = RGBColor(15, 23, 42)       # Slate 900 for body
TEXT_MUTED   = RGBColor(100, 116, 139)    # Slate 500 for secondary text
TEXT_LIGHT   = RGBColor(241, 245, 249)    # Slate 100 for dark backgrounds
BORDER_GRAY  = RGBColor(226, 232, 240)    # Slate 200 border stroke
CARD_BG      = RGBColor(255, 255, 255)    # White card fill
TAG_BG       = RGBColor(241, 245, 249)    # Light gray badge background
ACCENT_BLUE  = RGBColor(37, 99, 235)      # Royal accent

FONT_HEADING = "Segoe UI"
FONT_BODY    = "Segoe UI"
FONT_MONO    = "Consolas"

def set_flat_style(shape, fill_color, border_color=None, border_width=1):
    shape.fill.solid()
    shape.fill.fore_color.rgb = fill_color
    if border_color:
        shape.line.color.rgb = border_color
        shape.line.width = Pt(border_width)
    else:
        shape.line.fill.background()

def add_header(slide, title_text, category_text, subtitle_text):
    """Adds a standard scientific header banner to content slides."""
    # Top banner bar
    top_bar = slide.shapes.add_shape(MSO_SHAPE.RECTANGLE, Inches(0), Inches(0), Inches(13.333), Inches(1.18))
    set_flat_style(top_bar, NAVY_DARK)
    
    # Category Pill
    pill = slide.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(0.8), Inches(0.16), Inches(3.2), Inches(0.26))
    set_flat_style(pill, TEAL)
    tf_p = pill.text_frame
    tf_p.word_wrap = True
    tf_p.vertical_anchor = MSO_ANCHOR.MIDDLE
    p_p = tf_p.paragraphs[0]
    p_p.text = category_text.upper()
    p_p.font.name = FONT_HEADING
    p_p.font.size = Pt(9)
    p_p.font.bold = True
    p_p.font.color.rgb = WHITE
    p_p.alignment = PP_ALIGN.CENTER
    
    # Title Text
    tb_title = slide.shapes.add_textbox(Inches(0.8), Inches(0.46), Inches(11.733), Inches(0.42))
    tf_t = tb_title.text_frame
    tf_t.word_wrap = True
    tf_t.margin_top = Inches(0)
    tf_t.margin_bottom = Inches(0)
    tf_t.margin_left = Inches(0)
    p_t = tf_t.paragraphs[0]
    p_t.text = title_text
    p_t.font.name = FONT_HEADING
    p_t.font.size = Pt(19)
    p_t.font.bold = True
    p_t.font.color.rgb = WHITE
    
    # Subtitle Text
    tb_sub = slide.shapes.add_textbox(Inches(0.8), Inches(0.88), Inches(11.733), Inches(0.24))
    tf_s = tb_sub.text_frame
    tf_s.word_wrap = True
    tf_s.margin_top = Inches(0)
    tf_s.margin_bottom = Inches(0)
    tf_s.margin_left = Inches(0)
    p_s = tf_s.paragraphs[0]
    p_s.text = subtitle_text
    p_s.font.name = FONT_BODY
    p_s.font.size = Pt(10.5)
    p_s.font.color.rgb = GREEN_ACCENT

def add_footer(slide, current_slide, total_slides=13):
    """Adds a standard footer to content slides."""
    line = slide.shapes.add_shape(MSO_SHAPE.RECTANGLE, Inches(0.8), Inches(7.06), Inches(11.733), Inches(0.015))
    set_flat_style(line, BORDER_GRAY)
    
    tb = slide.shapes.add_textbox(Inches(0.8), Inches(7.12), Inches(8.5), Inches(0.25))
    tf = tb.text_frame
    tf.margin_left = Inches(0)
    p = tf.paragraphs[0]
    p.text = "BioGenome — DNA Sequence Analysis Using Data Structures & Algorithms | Academic Project for DSA-3"
    p.font.name = FONT_BODY
    p.font.size = Pt(9)
    p.font.color.rgb = TEXT_MUTED
    
    tb_num = slide.shapes.add_textbox(Inches(10.5), Inches(7.12), Inches(2.033), Inches(0.25))
    tf_n = tb_num.text_frame
    tf_n.margin_right = Inches(0)
    p_n = tf_n.paragraphs[0]
    p_n.text = f"Slide {current_slide} of {total_slides}"
    p_n.font.name = FONT_BODY
    p_n.font.size = Pt(9)
    p_n.font.bold = True
    p_n.font.color.rgb = TEAL
    p_n.alignment = PP_ALIGN.RIGHT

def set_slide_background(slide, color):
    bg = slide.shapes.add_shape(MSO_SHAPE.RECTANGLE, Inches(0), Inches(0), Inches(13.333), Inches(7.5))
    set_flat_style(bg, color)

def create_card_shape(slide, left, top, width, height, bg_color=WHITE, border_color=BORDER_GRAY, border_width=1):
    card = slide.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, left, top, width, height)
    set_flat_style(card, bg_color, border_color, border_width)
    return card

def format_cell(cell, text, bold=False, color=TEXT_DARK, size=9.5, align=PP_ALIGN.LEFT, bg_color=None):
    if bg_color:
        cell.fill.solid()
        cell.fill.fore_color.rgb = bg_color
    cell.vertical_anchor = MSO_ANCHOR.MIDDLE
    cell.margin_left = Inches(0.08)
    cell.margin_right = Inches(0.08)
    cell.margin_top = Inches(0.04)
    cell.margin_bottom = Inches(0.04)
    tf = cell.text_frame
    tf.word_wrap = True
    p = tf.paragraphs[0]
    p.text = text
    p.font.name = FONT_BODY
    p.font.size = Pt(size)
    p.font.bold = bold
    p.font.color.rgb = color
    p.alignment = align

# Initialize Presentation
prs = Presentation()
prs.slide_width = Inches(13.333)
prs.slide_height = Inches(7.5)
blank_layout = prs.slide_layouts[6]

# ==============================================================================
# SLIDE 1 — TITLE SLIDE
# ==============================================================================
s1 = prs.slides.add_slide(blank_layout)
set_slide_background(s1, NAVY_DARK)

# Subtle background geometric card
s1_accent_bg = s1.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(0.8), Inches(0.8), Inches(11.733), Inches(5.9))
set_flat_style(s1_accent_bg, NAVY_MEDIUM, TEAL, border_width=1.5)

# Subject Pill Tag
s1_tag = s1.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(1.4), Inches(1.3), Inches(3.2), Inches(0.35))
set_flat_style(s1_tag, TEAL)
tf_s1_tag = s1_tag.text_frame
tf_s1_tag.vertical_anchor = MSO_ANCHOR.MIDDLE
p_tag = tf_s1_tag.paragraphs[0]
p_tag.text = "ACADEMIC PROJECT — DSA-3"
p_tag.font.name = FONT_HEADING
p_tag.font.size = Pt(11)
p_tag.font.bold = True
p_tag.font.color.rgb = WHITE
p_tag.alignment = PP_ALIGN.CENTER

# Main Title
tb_s1_title = s1.shapes.add_textbox(Inches(1.4), Inches(1.85), Inches(6.8), Inches(1.2))
tf_s1_t = tb_s1_title.text_frame
tf_s1_t.word_wrap = True
p_s1_t = tf_s1_t.paragraphs[0]
p_s1_t.text = "BioGenome"
p_s1_t.font.name = FONT_HEADING
p_s1_t.font.size = Pt(50)
p_s1_t.font.bold = True
p_s1_t.font.color.rgb = WHITE

# Subtitle
tb_s1_sub = s1.shapes.add_textbox(Inches(1.4), Inches(3.05), Inches(6.8), Inches(0.9))
tf_s1_sub = tb_s1_sub.text_frame
tf_s1_sub.word_wrap = True
p_s1_sub = tf_s1_sub.paragraphs[0]
p_s1_sub.text = "DNA Sequence Analysis Using Data Structures & Algorithms"
p_s1_sub.font.name = FONT_HEADING
p_s1_sub.font.size = Pt(18)
p_s1_sub.font.bold = True
p_s1_sub.font.color.rgb = GREEN_ACCENT

# Project Summary Description
tb_s1_desc = s1.shapes.add_textbox(Inches(1.4), Inches(3.95), Inches(6.8), Inches(1.2))
tf_s1_desc = tb_s1_desc.text_frame
tf_s1_desc.word_wrap = True
p_s1_d = tf_s1_desc.paragraphs[0]
p_s1_d.text = (
    "A comprehensive computational biology platform powered by 23 algorithm implementations "
    "across 6 foundational DSA modules. Engineered with a high-performance Java 17+ backend engine, "
    "interactive 15-workflow scientific CLI runner, and decoupled educational web dashboard."
)
p_s1_d.font.name = FONT_BODY
p_s1_d.font.size = Pt(11.5)
p_s1_d.font.color.rgb = TEXT_LIGHT

# 4 Key Metrics Badges
badges = [
    ("6 Modules", "Algorithmic Pillars"),
    ("23 Algorithms", "Rigorous Implementations"),
    ("29 Unit Tests", "100% Automated Passing"),
    ("Dual Interface", "Scientific CLI + Web")
]
for idx, (b_top, b_sub) in enumerate(badges):
    bx = Inches(1.4 + idx * 1.65)
    b_card = s1.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, bx, Inches(5.35), Inches(1.5), Inches(0.85))
    set_flat_style(b_card, NAVY_DARK, TEAL_BORDER, border_width=1)
    tf_b = b_card.text_frame
    tf_b.word_wrap = True
    p1 = tf_b.paragraphs[0]
    p1.text = b_top
    p1.font.name = FONT_HEADING
    p1.font.size = Pt(12)
    p1.font.bold = True
    p1.font.color.rgb = GREEN_ACCENT
    p1.alignment = PP_ALIGN.CENTER
    p2 = tf_b.add_paragraph()
    p2.text = b_sub
    p2.font.name = FONT_BODY
    p2.font.size = Pt(8.5)
    p2.font.color.rgb = TEXT_LIGHT
    p2.alignment = PP_ALIGN.CENTER

# Right Side: Native Vector DNA Double Helix Illustration
dna_center_x = 10.2
dna_top_y = 1.45
dna_pairs = [
    ("A", "T", 0.0),
    ("T", "A", 0.5),
    ("G", "C", 0.9),
    ("C", "G", 1.0),
    ("A", "T", 0.7),
    ("G", "C", 0.2),
    ("T", "A", -0.4),
    ("C", "G", -0.9),
    ("A", "T", -1.0),
    ("G", "C", -0.7),
    ("T", "A", -0.2),
    ("C", "G", 0.4),
    ("A", "T", 0.9),
    ("T", "A", 1.0)
]

for i, (base_l, base_r, phase) in enumerate(dna_pairs):
    curr_y = Inches(dna_top_y + i * 0.35)
    spread = 1.35 * phase
    left_x = Inches(dna_center_x - spread)
    right_x = Inches(dna_center_x + spread)
    
    # Connecting base-pair rung
    rung = s1.shapes.add_shape(
        MSO_SHAPE.ROUNDED_RECTANGLE, 
        min(left_x, right_x) + Inches(0.18), 
        curr_y + Inches(0.08), 
        abs(right_x - left_x) + Inches(0.02), 
        Inches(0.04)
    )
    set_flat_style(rung, TEAL if i % 2 == 0 else GREEN_ACCENT)
    
    # Left Strand Node
    node_l = s1.shapes.add_shape(MSO_SHAPE.OVAL, left_x, curr_y, Inches(0.36), Inches(0.36))
    set_flat_style(node_l, TEAL, WHITE, border_width=1)
    tf_l = node_l.text_frame
    tf_l.vertical_anchor = MSO_ANCHOR.MIDDLE
    p_nl = tf_l.paragraphs[0]
    p_nl.text = base_l
    p_nl.font.name = FONT_MONO
    p_nl.font.size = Pt(9.5)
    p_nl.font.bold = True
    p_nl.font.color.rgb = WHITE
    p_nl.alignment = PP_ALIGN.CENTER
    
    # Right Strand Node
    node_r = s1.shapes.add_shape(MSO_SHAPE.OVAL, right_x, curr_y, Inches(0.36), Inches(0.36))
    set_flat_style(node_r, GREEN_ACCENT, WHITE, border_width=1)
    tf_r = node_r.text_frame
    tf_r.vertical_anchor = MSO_ANCHOR.MIDDLE
    p_nr = tf_r.paragraphs[0]
    p_nr.text = base_r
    p_nr.font.name = FONT_MONO
    p_nr.font.size = Pt(9.5)
    p_nr.font.bold = True
    p_nr.font.color.rgb = NAVY_DARK
    p_nr.alignment = PP_ALIGN.CENTER

# DNA Legend Tag
tb_dna_lbl = s1.shapes.add_textbox(Inches(8.5), Inches(6.35), Inches(3.6), Inches(0.35))
tf_lbl = tb_dna_lbl.text_frame
p_lbl = tf_lbl.paragraphs[0]
p_lbl.text = "DNA Base Complementarity: A=T (2 H-bonds) | G≡C (3 H-bonds)"
p_lbl.font.name = FONT_BODY
p_lbl.font.size = Pt(8.5)
p_lbl.font.color.rgb = TEXT_MUTED
p_lbl.alignment = PP_ALIGN.CENTER

print("Built Slide 1 (Title Slide)")

# ==============================================================================
# SLIDE 2 — PROJECT OVERVIEW & SCIENTIFIC MENTAL MODEL
# ==============================================================================
s2 = prs.slides.add_slide(blank_layout)
set_slide_background(s2, BG_LIGHT)
add_header(s2, "Project Overview: Computational Challenges in Genomics", "01. Introduction & Context", "Why rigorous Data Structures and Algorithms are indispensable for biological sequence processing")
add_footer(s2, 2)

# Left Column: The Biological Reality & Problem Scope (Card)
card_left = create_card_shape(s2, Inches(0.8), Inches(1.35), Inches(5.6), Inches(5.5))
tb_c1 = s2.shapes.add_textbox(Inches(1.0), Inches(1.5), Inches(5.2), Inches(5.2))
tf_c1 = tb_c1.text_frame
tf_c1.word_wrap = True

p = tf_c1.paragraphs[0]
p.text = "The Scale & Nature of Genomic Data"
p.font.name = FONT_HEADING
p.font.size = Pt(14)
p.font.bold = True
p.font.color.rgb = NAVY_DARK

bullets_c1 = [
    ("4 Nucleotide Bases: ", "DNA encodes genetic code as strings over alphabet Σ = {A, C, G, T} (Adenine, Cytosine, Guanine, Thymine)."),
    ("Massive Sequence Volume: ", "Human haploid genome spans 3.2 billion base pairs; next-generation sequencers generate terabytes of unorganized short reads daily."),
    ("Intractability of Naive Approaches: ", "O(NM) quadratic comparisons fail catastrophically on whole chromosomes (N ≈ 10^8 bp). Linear and sub-linear search is mandatory."),
    ("Biological Noise & Mutations: ", "Sequences contain insertions, deletions, point substitutions, and PCR transpositions requiring dynamic programming distance metrics."),
    ("Network Logistics: ", "Clinical sample processing involves complex diagnostic pathways, high-throughput pipelines, and scarce laboratory resources.")
]
for title, desc in bullets_c1:
    p = tf_c1.add_paragraph()
    p.space_before = Pt(8)
    run1 = p.add_run()
    run1.text = "• " + title
    run1.font.name = FONT_HEADING
    run1.font.bold = True
    run1.font.size = Pt(10)
    run1.font.color.rgb = TEAL
    run2 = p.add_run()
    run2.text = desc
    run2.font.name = FONT_BODY
    run2.font.size = Pt(9.5)
    run2.font.color.rgb = TEXT_DARK

# Right Column: The BioGenome Philosophy & Mental Model
card_right = create_card_shape(s2, Inches(6.6), Inches(1.35), Inches(5.933), Inches(5.5))
tb_c2 = s2.shapes.add_textbox(Inches(6.8), Inches(1.5), Inches(5.5), Inches(5.2))
tf_c2 = tb_c2.text_frame
tf_c2.word_wrap = True

p = tf_c2.paragraphs[0]
p.text = "The BioGenome Architectural Mental Model"
p.font.name = FONT_HEADING
p.font.size = Pt(14)
p.font.bold = True
p.font.color.rgb = NAVY_DARK

p_sub = tf_c2.add_paragraph()
p_sub.text = "BioGenome bridges biological problem-solving to classical computer science:"
p_sub.font.name = FONT_BODY
p_sub.font.size = Pt(10)
p_sub.font.color.rgb = TEXT_MUTED

# 3-Step Flow Diagram inside Right Card
steps = [
    ("1. BIOLOGICAL / SCIENTIFIC TASK", "Target Motif Discovery | Homology Alignment | Logistics Routing", NAVY_DARK),
    ("2. BIOINFORMATICS OPERATION", "Exact Pattern Matching | Local Alignment | Max-Flow Min-Cut", TEAL),
    ("3. COMPUTATIONAL ENGINE (DSA)", "KMP Algorithm | Smith-Waterman DP | Dinic's Blocking Flow", MINT_GREEN)
]
for s_idx, (s_title, s_ex, s_col) in enumerate(steps):
    step_y = Inches(2.3 + s_idx * 1.1)
    s_box = s2.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(7.0), step_y, Inches(5.133), Inches(0.85))
    set_flat_style(s_box, s_col, WHITE, border_width=1)
    tf_sb = s_box.text_frame
    tf_sb.vertical_anchor = MSO_ANCHOR.MIDDLE
    p_sb1 = tf_sb.paragraphs[0]
    p_sb1.text = s_title
    p_sb1.font.name = FONT_HEADING
    p_sb1.font.size = Pt(10.5)
    p_sb1.font.bold = True
    p_sb1.font.color.rgb = WHITE
    p_sb1.alignment = PP_ALIGN.CENTER
    p_sb2 = tf_sb.add_paragraph()
    p_sb2.text = s_ex
    p_sb2.font.name = FONT_MONO
    p_sb2.font.size = Pt(8.5)
    p_sb2.font.color.rgb = GREEN_ACCENT if s_col == NAVY_DARK else WHITE
    p_sb2.alignment = PP_ALIGN.CENTER
    
    if s_idx < 2:
        # Downward indicator arrow
        arrow = s2.shapes.add_shape(MSO_SHAPE.DOWN_ARROW, Inches(9.4), step_y + Inches(0.87), Inches(0.32), Inches(0.2))
        set_flat_style(arrow, TEAL)

# Bottom Key Note in right card
tb_note = s2.shapes.add_textbox(Inches(7.0), Inches(5.75), Inches(5.133), Inches(0.9))
tf_n = tb_note.text_frame
tf_n.word_wrap = True
pn = tf_n.paragraphs[0]
pn.text = "Educational Purpose: In BioGenome, algorithms are never presented in isolation. Every algorithm directly resolves an authentic computational bottleneck in genomic data analysis with theoretical guarantees."
pn.font.name = FONT_BODY
pn.font.size = Pt(9.5)
pn.font.color.rgb = TEXT_MUTED

print("Built Slide 2 (Project Overview)")

# ==============================================================================
# SLIDE 3 — ALGORITHM MODULES OVERVIEW (THE 6 PILLARS)
# ==============================================================================
s3 = prs.slides.add_slide(blank_layout)
set_slide_background(s3, BG_LIGHT)
add_header(s3, "Algorithm Modules Overview: The 6 Foundational Pillars", "02. Systematic Module Structure", "Complete inventory of 23 algorithm implementations organized by mathematical and algorithmic paradigm")
add_footer(s3, 3)

modules_data = [
    {
        "id": "MODULE 1",
        "title": "Exact String Matching & Motifs",
        "pkg": "com.biogenome.string",
        "count": "4 Algorithms",
        "algos": "• Knuth-Morris-Pratt (KMP)\n• Z-Algorithm (Prefix matching)\n• Rabin-Karp (Rolling Hash)\n• Aho-Corasick (Trie Automaton)",
        "purpose": "Promoter boxes, restriction enzyme screening & tandem repeat discovery."
    },
    {
        "id": "MODULE 2",
        "title": "Suffix Structures & Indexing",
        "pkg": "com.biogenome.suffix",
        "count": "4 Algorithms",
        "algos": "• Suffix Array (SA-IS Linear Time)\n• Kasai's LCP Algorithm\n• Compact Suffix Tree (Radix)\n• Suffix Automaton / SAM (DAWG)",
        "purpose": "Sub-linear whole-genome pattern search, repeat analysis & distinct factors."
    },
    {
        "id": "MODULE 3",
        "title": "Dynamic Programming & Alignment",
        "pkg": "com.biogenome.dp",
        "count": "4 Algorithms",
        "algos": "• Needleman-Wunsch (Global)\n• Smith-Waterman (Local Domain)\n• Damerau-Levenshtein Distance\n• Genomic Bitmask DP (Assembly)",
        "purpose": "End-to-end gene homology, indel error profiling & contig read assembly."
    },
    {
        "id": "MODULE 4",
        "title": "Network Flow & Bipartite Matching",
        "pkg": "com.biogenome.flow",
        "count": "4+ Algorithms",
        "algos": "• Ford-Fulkerson (DFS Augmenting)\n• Edmonds-Karp (BFS Shortest Path)\n• Dinic's Blocking Flow Algorithm\n• Hopcroft-Karp & König's Theorem",
        "purpose": "Laboratory specimen logistics, saturated cut diagnosis & trial assignments."
    },
    {
        "id": "MODULE 5",
        "title": "NP-Completeness & Reductions",
        "pkg": "com.biogenome.np",
        "count": "5 Implementations",
        "algos": "• DPLL 3-SAT Solver\n• 3-SAT → CLIQUE Reduction\n• CLIQUE → INDEPENDENT SET\n• Gallai Duality (IS ↔ VC)\n• Vertex Cover 2-Approximation",
        "purpose": "Multiplex assay scheduling, conflict-free markers & sensor coverage."
    },
    {
        "id": "MODULE 6",
        "title": "Randomized & Parallel Processing",
        "pkg": "com.biogenome.randparallel",
        "count": "6 Implementations",
        "algos": "• Randomized QuickSort\n• Reservoir Sampling (Alg R)\n• Miller-Rabin Primality Test\n• Blelloch Parallel Prefix Scan\n• Parallel Tree Reduce & Brent's Law",
        "purpose": "Sequencer read library sorting, continuous streaming & multi-core scaling."
    }
]

# 2 Rows of 3 Cards
card_w = Inches(3.72)
card_h = Inches(2.62)
for idx, m in enumerate(modules_data):
    row = idx // 3
    col = idx % 3
    cx = Inches(0.8 + col * 4.0)
    cy = Inches(1.35 + row * 2.8)
    
    c = create_card_shape(s3, cx, cy, card_w, card_h)
    
    # Top badge bar
    top_badge = s3.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, cx + Inches(0.12), cy + Inches(0.12), Inches(1.1), Inches(0.24))
    set_flat_style(top_badge, TEAL if row == 0 else MINT_GREEN)
    tf_tb = top_badge.text_frame
    tf_tb.vertical_anchor = MSO_ANCHOR.MIDDLE
    ptb = tf_tb.paragraphs[0]
    ptb.text = m["id"]
    ptb.font.name = FONT_HEADING
    ptb.font.size = Pt(8.5)
    ptb.font.bold = True
    ptb.font.color.rgb = WHITE
    ptb.alignment = PP_ALIGN.CENTER
    
    # Package path
    tb_pkg = s3.shapes.add_textbox(cx + Inches(1.3), cy + Inches(0.1), Inches(2.3), Inches(0.24))
    tf_pkg = tb_pkg.text_frame
    ppkg = tf_pkg.paragraphs[0]
    ppkg.text = m["count"]
    ppkg.font.name = FONT_HEADING
    ppkg.font.size = Pt(8.5)
    ppkg.font.bold = True
    ppkg.font.color.rgb = TEAL
    ppkg.alignment = PP_ALIGN.RIGHT
    
    # Title
    tb_t = s3.shapes.add_textbox(cx + Inches(0.12), cy + Inches(0.38), Inches(3.48), Inches(0.35))
    tf_t = tb_t.text_frame
    tf_t.word_wrap = True
    pt = tf_t.paragraphs[0]
    pt.text = m["title"]
    pt.font.name = FONT_HEADING
    pt.font.size = Pt(11)
    pt.font.bold = True
    pt.font.color.rgb = NAVY_DARK
    
    # Algorithms list
    tb_alg = s3.shapes.add_textbox(cx + Inches(0.12), cy + Inches(0.75), Inches(3.48), Inches(1.15))
    tf_alg = tb_alg.text_frame
    tf_alg.word_wrap = True
    palg = tf_alg.paragraphs[0]
    palg.text = m["algos"]
    palg.font.name = FONT_BODY
    palg.font.size = Pt(8.8)
    palg.font.color.rgb = TEXT_DARK
    
    # Purpose Footer
    purp_box = s3.shapes.add_shape(MSO_SHAPE.RECTANGLE, cx + Inches(0.08), cy + Inches(1.98), Inches(3.56), Inches(0.55))
    set_flat_style(purp_box, BG_LIGHT, BORDER_GRAY, border_width=0.5)
    tf_purp = purp_box.text_frame
    tf_purp.word_wrap = True
    ppurp = tf_purp.paragraphs[0]
    ppurp.text = "Goal: " + m["purpose"]
    ppurp.font.name = FONT_BODY
    ppurp.font.size = Pt(8)
    ppurp.font.color.rgb = TEXT_MUTED

print("Built Slide 3 (Algorithm Modules Overview)")

# ==============================================================================
# HELPER: GENERIC MODULE SLIDE BUILDER (SLIDES 4 - 9)
# ==============================================================================
def build_module_slide(prs, slide_num, mod_pill, mod_title, mod_subtitle, objective_text, algos_detail, demo_box_title, demo_box_content, complexity_table_data):
    s = prs.slides.add_slide(blank_layout)
    set_slide_background(s, BG_LIGHT)
    add_header(s, mod_title, mod_pill, mod_subtitle)
    add_footer(s, slide_num)
    
    # Top Objective Bar
    obj_card = create_card_shape(s, Inches(0.8), Inches(1.3), Inches(11.733), Inches(0.68), bg_color=WHITE, border_color=TEAL_BORDER)
    tb_obj = s.shapes.add_textbox(Inches(0.95), Inches(1.33), Inches(11.4), Inches(0.6))
    tf_obj = tb_obj.text_frame
    tf_obj.word_wrap = True
    p_o1 = tf_obj.paragraphs[0]
    r_ot = p_o1.add_run()
    r_ot.text = "MODULE SCIENTIFIC OBJECTIVE: "
    r_ot.font.name = FONT_HEADING
    r_ot.font.size = Pt(10)
    r_ot.font.bold = True
    r_ot.font.color.rgb = TEAL
    r_od = p_o1.add_run()
    r_od.text = objective_text
    r_od.font.name = FONT_BODY
    r_od.font.size = Pt(9.5)
    r_od.font.color.rgb = TEXT_DARK
    
    # Left Column: Detailed Algorithm Cards (2x2 or Stack)
    card_w = Inches(3.75)
    card_h = Inches(2.25)
    for idx, a in enumerate(algos_detail[:4]):
        r = idx // 2
        c = idx % 2
        ax = Inches(0.8 + c * 3.95)
        ay = Inches(2.1 + r * 2.38)
        
        acard = create_card_shape(s, ax, ay, card_w, card_h)
        
        # Title & Method
        tb_a = s.shapes.add_textbox(ax + Inches(0.12), ay + Inches(0.08), Inches(3.51), Inches(2.1))
        tf_a = tb_a.text_frame
        tf_a.word_wrap = True
        
        pa_t = tf_a.paragraphs[0]
        pa_t.text = a["name"]
        pa_t.font.name = FONT_HEADING
        pa_t.font.size = Pt(10.5)
        pa_t.font.bold = True
        pa_t.font.color.rgb = NAVY_DARK
        
        pa_p = tf_a.add_paragraph()
        pa_p.space_before = Pt(3)
        rp1 = pa_p.add_run()
        rp1.text = "Working Principle: "
        rp1.font.name = FONT_HEADING
        rp1.font.bold = True
        rp1.font.size = Pt(8.5)
        rp1.font.color.rgb = TEAL
        rp2 = pa_p.add_run()
        rp2.text = a["principle"]
        rp2.font.name = FONT_BODY
        rp2.font.size = Pt(8.3)
        rp2.font.color.rgb = TEXT_DARK
        
        pa_a = tf_a.add_paragraph()
        pa_a.space_before = Pt(3)
        ra1 = pa_a.add_run()
        ra1.text = "DNA Application: "
        ra1.font.name = FONT_HEADING
        ra1.font.bold = True
        ra1.font.size = Pt(8.5)
        ra1.font.color.rgb = MINT_GREEN
        ra2 = pa_a.add_run()
        ra2.text = a["app"]
        ra2.font.name = FONT_BODY
        ra2.font.size = Pt(8.3)
        ra2.font.color.rgb = TEXT_DARK
        
        pa_c = tf_a.add_paragraph()
        pa_c.space_before = Pt(3)
        rc1 = pa_c.add_run()
        rc1.text = f"Complexity: Time {a['time']} | Space {a['space']}"
        rc1.font.name = FONT_MONO
        rc1.font.bold = True
        rc1.font.size = Pt(8.0)
        rc1.font.color.rgb = TEXT_MUTED

    # Right Column: Visual Demonstration Card & Complexity Summary Table
    rx = Inches(8.85)
    rw = Inches(3.683)
    
    # Top Demonstration Box
    demo_card = create_card_shape(s, rx, Inches(2.1), rw, Inches(2.2), bg_color=NAVY_DARK, border_color=TEAL)
    tb_demo = s.shapes.add_textbox(rx + Inches(0.12), Inches(2.18), rw - Inches(0.24), Inches(2.0))
    tf_demo = tb_demo.text_frame
    tf_demo.word_wrap = True
    
    p_dt = tf_demo.paragraphs[0]
    p_dt.text = demo_box_title.upper()
    p_dt.font.name = FONT_HEADING
    p_dt.font.size = Pt(9.5)
    p_dt.font.bold = True
    p_dt.font.color.rgb = GREEN_ACCENT
    
    p_dc = tf_demo.add_paragraph()
    p_dc.space_before = Pt(4)
    p_dc.text = demo_box_content
    p_dc.font.name = FONT_MONO
    p_dc.font.size = Pt(8.2)
    p_dc.font.color.rgb = WHITE
    
    # Bottom Table: Complexity Profile
    table_card = create_card_shape(s, rx, Inches(4.45), rw, Inches(2.45))
    tb_tbl_title = s.shapes.add_textbox(rx + Inches(0.1), Inches(4.5), rw - Inches(0.2), Inches(0.25))
    tf_tt = tb_tbl_title.text_frame
    ptt = tf_tt.paragraphs[0]
    ptt.text = "Theoretical Complexity Profile"
    ptt.font.name = FONT_HEADING
    ptt.font.size = Pt(9.5)
    ptt.font.bold = True
    ptt.font.color.rgb = NAVY_DARK
    
    # Add Table
    rows = len(complexity_table_data) + 1
    cols = 3
    t_shape = s.shapes.add_table(rows, cols, rx + Inches(0.08), Inches(4.8), rw - Inches(0.16), Inches(2.0))
    table = t_shape.table
    table.columns[0].width = Inches(1.5)
    table.columns[1].width = Inches(1.1)
    table.columns[2].width = Inches(0.9)
    
    # Headers
    format_cell(table.cell(0, 0), "Algorithm", bold=True, color=WHITE, size=8.5, bg_color=NAVY_DARK)
    format_cell(table.cell(0, 1), "Time (Worst)", bold=True, color=WHITE, size=8.5, bg_color=NAVY_DARK)
    format_cell(table.cell(0, 2), "Space", bold=True, color=WHITE, size=8.5, bg_color=NAVY_DARK)
    
    for r_i, row_data in enumerate(complexity_table_data):
        bg_c = BG_LIGHT if r_i % 2 == 1 else WHITE
        format_cell(table.cell(r_i + 1, 0), row_data[0], bold=True, color=TEXT_DARK, size=8.0, bg_color=bg_c)
        format_cell(table.cell(r_i + 1, 1), row_data[1], bold=False, color=TEAL, size=8.0, bg_color=bg_c)
        format_cell(table.cell(r_i + 1, 2), row_data[2], bold=False, color=TEXT_MUTED, size=8.0, bg_color=bg_c)

    return s

# ==============================================================================
# SLIDE 4 — MODULE 1: EXACT STRING MATCHING & MOTIF DISCOVERY
# ==============================================================================
m1_algos = [
    {
        "name": "1. Knuth-Morris-Pratt (KMP)",
        "principle": "Computes prefix function (π-table / LPS) to shift pattern without backtracking text index upon mismatch.",
        "app": "Exact promoter box detection (e.g., TATA-box, Pribnow box in E. coli oriC replication origin).",
        "time": "O(N + M)",
        "space": "O(M)"
    },
    {
        "name": "2. Z-Algorithm",
        "principle": "Computes Z-array where Z[i] is the length of longest prefix match starting at position i using a moving [L, R] window.",
        "app": "Tandem repeat detection, short tandem repeats (STRs), and periodic viral genome boundaries.",
        "time": "O(N + M)",
        "space": "O(N + M)"
    },
    {
        "name": "3. Rabin-Karp Rolling Hash",
        "principle": "Calculates rolling polynomial hash over sliding windows in O(1) time per shift with character verification upon hash match.",
        "app": "Dinucleotide frequency profiling (CpG island discovery) and multiple oligonucleotide occurrences.",
        "time": "Avg O(N+M), Worst O(NM)",
        "space": "O(1)"
    },
    {
        "name": "4. Aho-Corasick Automaton",
        "principle": "Constructs a trie with BFS failure and dictionary output links, screening an entire dictionary simultaneously in a single pass.",
        "app": "Multi-motif restriction enzyme library screening (EcoRI, AluI, HindIII, BamHI cleavage mapping).",
        "time": "O(N + ΣM_i + K)",
        "space": "O(ΣM_i · |Σ|)"
    }
]

m1_demo_title = "Motif Traceback & π-Table Example"
m1_demo_content = (
    "Target Motif: 'ACCAC' (Length M=5)\n"
    "Pattern:  A  C  C  A  C\n"
    "π-Table: [0, 0, 0, 1, 2]  (LPS)\n\n"
    "Text: ... G A T C A C C A C T T ...\n"
    "Match: Mismatch at base 4 shifts pattern\n"
    "using π[3]=1 without backtracking.\n\n"
    "Aho-Corasick Trie: Screens 8 enzymes\n"
    "(EcoRI, AluI, HindIII) in 1 linear pass!"
)

m1_table = [
    ("KMP Algorithm", "O(N + M)", "O(M)"),
    ("Z-Algorithm", "O(N + M)", "O(N + M)"),
    ("Rabin-Karp", "O(N · M)", "O(1)"),
    ("Aho-Corasick", "O(N + ΣM_i + K)", "O(ΣM_i · |Σ|)")
]

build_module_slide(
    prs, 4, "Module 1 : String Matching",
    "Module 1 — Exact String Matching & DNA Motif Discovery",
    "Sub-quadratic search, dictionary automata, and restriction enzyme cleavage mapping",
    "Identify regulatory promoter boxes, restriction endonuclease recognition sites, and repeated oligonucleotides without redundant backtracking.",
    m1_algos, m1_demo_title, m1_demo_content, m1_table
)
print("Built Slide 4 (Module 1)")

# ==============================================================================
# SLIDE 5 — MODULE 2: SUFFIX STRUCTURES & WHOLE-GENOME INDEXING
# ==============================================================================
m2_algos = [
    {
        "name": "1. Suffix Array Construction (SA-IS)",
        "principle": "Lexicographically sorts all chromosome suffixes. SA-IS induces sorting via S/L-type classification and LMS characters.",
        "app": "Whole-genome indexing enabling arbitrary query searches in O(M log N) via binary search or O(M) with LCP.",
        "time": "O(N) Linear Time",
        "space": "O(N)"
    },
    {
        "name": "2. Kasai's LCP Algorithm",
        "principle": "Computes Longest Common Prefix array in linear time using rank inversion property: LCP[rank[i]] ≥ LCP[rank[i-1]] - 1.",
        "app": "Discovery of maximal repeated sequences, gene duplications, and copy number variations.",
        "time": "O(N)",
        "space": "O(N)"
    },
    {
        "name": "3. Compact Suffix Tree",
        "principle": "Radix tree of all sequence suffixes where internal nodes branch on distinct characters and edges store substrings.",
        "app": "All-substring factor analysis, finding unique PCR primers, and identifying maximal palindromes.",
        "time": "O(N) Construction",
        "space": "O(N)"
    },
    {
        "name": "4. Suffix Automaton / SAM (DAWG)",
        "principle": "Minimal deterministic finite automaton (DFA) recognizing all factors of a sequence with O(N) states and transitions.",
        "app": "Counting distinct genomic subsegments and finding Longest Common Substrings (LCS) across strains.",
        "time": "O(N) Construction",
        "space": "O(N)"
    }
]

m2_demo_title = "Suffix Array & LCP Construction"
m2_demo_content = (
    "Genome: 'GATAGACA$' (N=8 bp)\n\n"
    "i  SA[i]  LCP[i]  Suffix String\n"
    "0    8      0     $\n"
    "1    7      0     A$\n"
    "2    5      1     ACA$\n"
    "3    3      1     AGACA$\n"
    "4    1      3     ATAGACA$\n"
    "5    6      0     CA$\n"
    "6    4      2     GACA$\n"
    "7    2      0     GATAGACA$\n"
    "8    0      2     GATAGACA$\n\n"
    "Result: Sub-linear search in O(M log N)!"
)

m2_table = [
    ("Suffix Array (SA-IS)", "O(N)", "O(N)"),
    ("Kasai's LCP", "O(N)", "O(N)"),
    ("Suffix Tree", "O(N)", "O(N)"),
    ("Suffix Automaton", "O(N)", "O(N)")
]

build_module_slide(
    prs, 5, "Module 2 : Suffix Indexing",
    "Module 2 — Suffix Structures & Whole-Genome Indexing",
    "Sub-linear whole-chromosome pattern retrieval, repeat discovery, and factor automata",
    "Index entire reference chromosomes to enable sub-linear sequence retrieval, detect tandem duplicated regions, and represent all substring factors.",
    m2_algos, m2_demo_title, m2_demo_content, m2_table
)
print("Built Slide 5 (Module 2)")

# ==============================================================================
# SLIDE 6 — MODULE 3: DYNAMIC PROGRAMMING & SEQUENCE ALIGNMENT
# ==============================================================================
m3_algos = [
    {
        "name": "1. Needleman-Wunsch Alignment",
        "principle": "Global dynamic programming alignment matrix with scoring parameters: Match (+2), Mismatch (-1), Gap (-2), and traceback.",
        "app": "End-to-end gene homology (e.g. Normal HbA vs Sickle Cell HbS allele conservation and point mutations).",
        "time": "O(N · M)",
        "space": "O(N · M)"
    },
    {
        "name": "2. Smith-Waterman Alignment",
        "principle": "Local dynamic programming alignment resetting negative cell values to 0; traceback starts at maximum matrix value.",
        "app": "Identifying conserved functional domains across divergent genomes (e.g. SARS-CoV-2 Spike RBD homology).",
        "time": "O(N · M)",
        "space": "O(N · M)"
    },
    {
        "name": "3. Damerau-Levenshtein Distance",
        "principle": "Extends edit distance by supporting adjacent character transpositions (swaps) at unit cost alongside insert, delete, and replace.",
        "app": "Sequencing error profiling: Distinguishing PCR optical transpositions from true biological double-substitutions.",
        "time": "O(N · M)",
        "space": "O(N · M)"
    },
    {
        "name": "4. Genomic Bitmask DP (SCS)",
        "principle": "Shortest Common Superstring (SCS) via integer bitmasks representing subsets of merged sequencing reads with maximal overlap.",
        "app": "De novo contig assembly: Reconstructing short sequencing fragments into the minimal genomic sequence.",
        "time": "O(K² · 2^K)",
        "space": "O(K · 2^K)"
    }
]

m3_demo_title = "DP Matrix & Read Assembly Trace"
m3_demo_content = (
    "Global Alignment Traceback:\n"
    "Seq 1: A  T  G  C  G  T  A  A  C  T\n"
    "       |  |  |  |  |  |  |     |  |\n"
    "Seq 2: A  T  -  C  G  T  A  A  T  T\n"
    "Score: +14 | Identity: 88.9%\n\n"
    "Bitmask Read Assembly:\n"
    "Reads: {'ATGC', 'TGCA', 'GCAT'}\n"
    "Optimal Overlap Bitmask: S=111_2\n"
    "Assembled Contig: 'ATGCAT' (6 bp)\n"
    "Overlap Saved: 6 bp of redundant sequencing"
)

m3_table = [
    ("Needleman-Wunsch", "O(N · M)", "O(N · M)"),
    ("Smith-Waterman", "O(N · M)", "O(N · M)"),
    ("Damerau-Levenshtein", "O(N · M)", "O(N · M)"),
    ("Genomic Bitmask DP", "O(K² · 2^K)", "O(K · 2^K)")
]

build_module_slide(
    prs, 6, "Module 3 : Dynamic Programming",
    "Module 3 — Dynamic Programming & Sequence Alignment",
    "Global homology, local conserved domains, transposition profiling, and contig assembly",
    "Quantify evolutionary divergence, profile sequencing platform errors, and assemble fragmented sequencing reads into continuous genomic contigs.",
    m3_algos, m3_demo_title, m3_demo_content, m3_table
)
print("Built Slide 6 (Module 3)")

# ==============================================================================
# SLIDE 7 — MODULE 4: NETWORK FLOW & BIPARTITE MATCHING
# ==============================================================================
m4_algos = [
    {
        "name": "1. Ford-Fulkerson Algorithm",
        "principle": "Augmenting path method iteratively pushing flow along DFS paths in the residual network until no augmenting path remains.",
        "app": "Calculating specimen throughput in multi-tier laboratory networks (Prep Labs → Sequencers → Analytics).",
        "time": "O(E · |f*|)",
        "space": "O(V + E)"
    },
    {
        "name": "2. Edmonds-Karp Algorithm",
        "principle": "Shortest augmenting path implementation using BFS, guaranteeing polynomial-time convergence independent of edge capacities.",
        "app": "Cold-chain specimen routing with minimal transfer stages, reducing biological degradation risk.",
        "time": "O(V · E²)",
        "space": "O(V + E)"
    },
    {
        "name": "3. Dinic's Blocking Flow",
        "principle": "Constructs layered level graphs using BFS, saturating paths in batches using DFS blocking flows to achieve fast execution.",
        "app": "Routing high-volume biological samples through large clinical trial facility networks.",
        "time": "O(V² · E)",
        "space": "O(V + E)"
    },
    {
        "name": "4. Hopcroft-Karp & König's Theorem",
        "principle": "Finds maximum bipartite matching via alternating BFS/DFS trees; verifies |Max Matching| = |Min Vertex Cover| (König).",
        "app": "Optimal researcher-to-experiment assignment and conflict resolution across clinical sequencing protocols.",
        "time": "O(E · √V)",
        "space": "O(V + E)"
    }
]

m4_demo_title = "Max-Flow Min-Cut & Duality Theorem"
m4_demo_content = (
    "Laboratory Logistics Network:\n"
    "Source (S) -> Labs (U, V) -> Sequencers (T)\n\n"
    "Max Net Flow Computed: 20 units/day\n"
    "Min-Cut Saturated Edges:\n"
    "  • Lab-U -> Sequencer-T (Cap: 10, Flow: 10)\n"
    "  • Lab-V -> Sequencer-T (Cap: 10, Flow: 10)\n\n"
    "Min-Cut Capacity: 20 units/day\n"
    "Theorem Proved: Max-Flow == Min-Cut!\n"
    "Identifies exact facility bottleneck edges."
)

m4_table = [
    ("Ford-Fulkerson", "O(E · |f*|)", "O(V + E)"),
    ("Edmonds-Karp", "O(V · E²)", "O(V + E)"),
    ("Dinic's Algorithm", "O(V² · E)", "O(V + E)"),
    ("Hopcroft-Karp Matching", "O(E · √V)", "O(V + E)")
]

build_module_slide(
    prs, 7, "Module 4 : Network Flow",
    "Module 4 — Network Flow & Bipartite Matching",
    "Clinical specimen logistics, bottleneck detection, and personnel assignment",
    "Model clinical laboratory logistics as capacitated flow networks, maximize specimen throughput, identify bottleneck capacities, and resolve resource conflicts.",
    m4_algos, m4_demo_title, m4_demo_content, m4_table
)
print("Built Slide 7 (Module 4)")

# ==============================================================================
# SLIDE 8 — MODULE 5: NP-COMPLETENESS & APPROXIMATIONS
# ==============================================================================
m5_algos = [
    {
        "name": "1. DPLL 3-SAT Solver",
        "principle": "Davis-Putnam-Logemann-Loveland backtracking search with unit clause propagation and pure literal elimination.",
        "app": "Multiplex assay primer compatibility verification and constraint scheduling under strict biochemical conflicts.",
        "time": "O(2^V) Exponential",
        "space": "O(V + C)"
    },
    {
        "name": "2. 3-SAT → CLIQUE Karp Reduction",
        "principle": "Constructs 3-partite clause gadget graph; connects non-contradictory literals across distinct clauses with compatibility edges.",
        "app": "Proving that finding mutually consistent biological parameter choices is equivalent to discovering a k-clique in G.",
        "time": "O(C²) Poly-time",
        "space": "O(C²)"
    },
    {
        "name": "3. CLIQUE → INDEPENDENT SET",
        "principle": "Graph complementation duality (G → G_bar): S is a Clique in G if and only if S is an Independent Set in G_bar.",
        "app": "Conflict-free genomic marker selection: Finding maximum sets of non-interacting genetic markers.",
        "time": "O(V²)",
        "space": "O(V²)"
    },
    {
        "name": "4. Vertex Cover 2-Approximation",
        "principle": "Maximal matching greedy heuristic taking both endpoints of mutually disjoint edges; guarantees cover size ≤ 2 × OPT.",
        "app": "Fast linear-time monitoring sensor placement across large oncogenic interaction networks (TP53, BRCA1, ATM).",
        "time": "O(V + E) Poly-time",
        "space": "O(V)"
    }
]

m5_demo_title = "The Formal Karp Reduction Chain"
m5_demo_content = (
    "Biological Constraint Formulation:\n"
    "    3-SAT Formula (Multiplex Constraints)\n"
    "             ↓  (Karp Reduction: O(C²))\n"
    "    CLIQUE Problem (Mutual Cooperation)\n"
    "             ↓  (Graph Complementation)\n"
    "    INDEPENDENT SET (Conflict-Free Markers)\n"
    "             ↓  (Gallai's Identity: |IS|+|VC|=|V|)\n"
    "    VERTEX COVER (Target Network Coverage)\n"
    "             ↓  (Maximal Matching Heuristic)\n"
    "    2-Approximation (Near-Optimal Sensor Placements)"
)

m5_table = [
    ("DPLL 3-SAT Solver", "O(2^V)", "O(V + C)"),
    ("3-SAT → CLIQUE", "O(C²)", "O(C²)"),
    ("CLIQUE → Indep. Set", "O(V²)", "O(V²)"),
    ("Vertex Cover 2-Approx", "O(V + E)", "O(V)")
]

build_module_slide(
    prs, 8, "Module 5 : NP-Completeness",
    "Module 5 — NP-Completeness, Karp Reductions & Approximations",
    "Multiplex assay constraint satisfaction, polynomial-time reductions, and approximation bounds",
    "Demonstrate NP-completeness in bioinformatics, execute formal polynomial-time Karp reductions, and deploy polynomial-time 2-approximation algorithms.",
    m5_algos, m5_demo_title, m5_demo_content, m5_table
)
print("Built Slide 8 (Module 5)")

# ==============================================================================
# SLIDE 9 — MODULE 6: RANDOMIZED & PARALLEL ALGORITHMS
# ==============================================================================
m6_algos = [
    {
        "name": "1. Randomized QuickSort",
        "principle": "Selects pivots uniformly at random, eliminating worst-case O(N²) vulnerabilities on pre-sorted biological read records.",
        "app": "Sorting large sequencing read libraries by length or GC-content without adversarial degradation.",
        "time": "Expected O(N log N)",
        "space": "O(log N)"
    },
    {
        "name": "2. Reservoir Sampling (Algorithm R)",
        "principle": "Maintains an unbiased random sample of size k from an unbounded data stream with replacement probability k / i for the i-th item.",
        "app": "Uniform real-time sampling of continuous sequencer observation streams without storing the entire dataset.",
        "time": "O(N) Single-Pass",
        "space": "O(k)"
    },
    {
        "name": "3. Miller-Rabin Primality Test",
        "principle": "Probabilistic primality test based on Fermat's Little Theorem and non-trivial square roots of unity, error bounded by 4^(-k).",
        "app": "Cryptogenomic security: Generating large prime keys for secure patient genomic data sharing.",
        "time": "O(k · log³ N)",
        "space": "O(1)"
    },
    {
        "name": "4. Blelloch Parallel Prefix Scan",
        "principle": "Work-efficient two-pass parallel tree algorithm (up-sweep reduce + down-sweep distribute) with O(N) work and O(log N) span.",
        "app": "Parallel computation of cumulative chromosomal base coverage and alignment coordinate offsets.",
        "time": "O(N) Work / O(log N) Span",
        "space": "O(N)"
    }
]

m6_demo_title = "Blelloch Scan & Brent's Scalability"
m6_demo_content = (
    "Blelloch Parallel Scan (Base Coverage):\n"
    "Input Coverage: [3, 1, 7, 0, 4, 1, 6, 3]\n"
    "Up-Sweep:       Parallel tree reduction\n"
    "Down-Sweep:     Parallel prefix distribution\n"
    "Exclusive Sum:  [0, 3, 4, 11, 11, 15, 16, 22]\n"
    "Inclusive Sum:  [3, 4, 11, 11, 15, 16, 22, 25]\n\n"
    "Brent's Law Multi-Core Bound:\n"
    "Work W=1000, Span S=50, Processors P=16\n"
    "Tp ≤ (W - S)/P + S = 109.4 time units\n"
    "Achieved Speedup: 9.14x faster (57.1% efficiency)"
)

m6_table = [
    ("Randomized QuickSort", "O(N log N)", "O(log N)"),
    ("Reservoir Sampling", "O(N)", "O(k)"),
    ("Miller-Rabin Test", "O(k log³ N)", "O(1)"),
    ("Blelloch Parallel Scan", "O(N) work", "O(N)")
]

build_module_slide(
    prs, 9, "Module 6 : Randomized & Parallel",
    "Module 6 — Randomized & Parallel High-Throughput Algorithms",
    "Streaming data sampling, parallel chromosomal prefix scans, and multi-core scaling bounds",
    "Tackle high-throughput next-generation sequencing data with randomized efficiency, cryptogenomic security, and work-efficient parallel tree algorithms.",
    m6_algos, m6_demo_title, m6_demo_content, m6_table
)
print("Built Slide 9 (Module 6)")

# ==============================================================================
# SLIDE 10 — SYSTEM ARCHITECTURE
# ==============================================================================
s10 = prs.slides.add_slide(blank_layout)
set_slide_background(s10, BG_LIGHT)
add_header(s10, "System Architecture: Decoupled Multi-Tier Platform", "03. System Architecture", "High-level design showing data flow, component interactions, and computational pipeline")
add_footer(s10, 10)

# 4 Horizontal Tier Cards
tiers = [
    {
        "num": "TIER 1",
        "name": "User & Interface Layer",
        "color": NAVY_DARK,
        "bg": WHITE,
        "items": [
            ("Interactive Scientific CLI Runner", "Main.java — 15-option workflow menu with ANSI formatting & Eclipse terminal support"),
            ("Decoupled Scientific Web Dashboard", "frontend/index.html — Vanilla CSS/JS laboratory visualizers & educational exploration views")
        ]
    },
    {
        "num": "TIER 2",
        "name": "Application & Orchestration Layer",
        "color": TEAL,
        "bg": WHITE,
        "items": [
            ("REST API Controller (Spring Boot)", "com.biogenome.controller.* — High-performance JSON endpoints (localhost:8080/api/v1)"),
            ("Biological Data & History Service", "BioGenomeDataService.java — In-memory reference repository, variant store & session history audit")
        ]
    },
    {
        "num": "TIER 3",
        "name": "Core DSA Computational Engine (6 Modules / 23 Algorithms)",
        "color": MINT_GREEN,
        "bg": WHITE,
        "items": [
            ("M1: String Algorithms", "KMP, Z-Algorithm, Rabin-Karp, Aho-Corasick"),
            ("M2: Suffix Structures", "Suffix Array (SA-IS), Kasai LCP, Suffix Tree, SAM"),
            ("M3: Dynamic Programming", "Needleman-Wunsch, Smith-Waterman, Damerau-Levenshtein, Bitmask DP"),
            ("M4: Network Flow", "Ford-Fulkerson, Edmonds-Karp, Dinic's Algorithm, Hopcroft-Karp, Min-Cut"),
            ("M5: NP-Completeness", "DPLL 3-SAT, 3-SAT→CLIQUE, CLIQUE→IS, Gallai Duality, Vertex Cover 2-Approx"),
            ("M6: Randomized & Parallel", "Randomized QuickSort, Reservoir Sampling, Miller-Rabin, Blelloch Scan, Brent's Law")
        ]
    },
    {
        "num": "TIER 4",
        "name": "Data Storage, Reference Datasets & Automated Verification",
        "color": NAVY_MEDIUM,
        "bg": WHITE,
        "items": [
            ("Model Organism Reference Sequences", "E. coli oriC (467 bp), Human HBB Wild-Type (HbA) vs Sickle Cell (HbS), SARS-CoV-2 Spike RBD"),
            ("Clinical Variant Catalog & Logistics", "Pathogenic SNPs in BRCA1, TP53, CFTR, EGFR | Directed multi-tier specimen facility networks"),
            ("Automated JUnit 5 Test Suite", "29/29 automated unit tests verifying mathematical correctness and algorithmic edge cases")
        ]
    }
]

tier_y_coords = [1.32, 2.45, 3.65, 5.35]
tier_heights  = [0.98, 1.05, 1.55, 1.45]

for t_idx, t in enumerate(tiers):
    ty = Inches(tier_y_coords[t_idx])
    th = Inches(tier_heights[t_idx])
    
    tcard = create_card_shape(s10, Inches(0.8), ty, Inches(11.733), th)
    
    # Left Header Strip
    strip = s10.shapes.add_shape(MSO_SHAPE.RECTANGLE, Inches(0.8), ty, Inches(2.6), th)
    set_flat_style(strip, t["color"])
    tf_str = strip.text_frame
    tf_str.word_wrap = True
    tf_str.vertical_anchor = MSO_ANCHOR.MIDDLE
    p_num = tf_str.paragraphs[0]
    p_num.text = t["num"]
    p_num.font.name = FONT_HEADING
    p_num.font.size = Pt(8.5)
    p_num.font.bold = True
    p_num.font.color.rgb = GREEN_ACCENT
    p_num.alignment = PP_ALIGN.CENTER
    
    p_nm = tf_str.add_paragraph()
    p_nm.text = t["name"]
    p_nm.font.name = FONT_HEADING
    p_nm.font.size = Pt(10)
    p_nm.font.bold = True
    p_nm.font.color.rgb = WHITE
    p_nm.alignment = PP_ALIGN.CENTER
    
    # Right Content Details
    tb_cnt = s10.shapes.add_textbox(Inches(3.55), ty + Inches(0.06), Inches(8.8), th - Inches(0.12))
    tf_cnt = tb_cnt.text_frame
    tf_cnt.word_wrap = True
    
    for i_idx, item in enumerate(t["items"]):
        p_it = tf_cnt.paragraphs[0] if i_idx == 0 else tf_cnt.add_paragraph()
        if i_idx > 0 and t_idx != 2:
            p_it.space_before = Pt(3)
        r_title = p_it.add_run()
        r_title.text = "• " + item[0] + ": "
        r_title.font.name = FONT_HEADING
        r_title.font.bold = True
        r_title.font.size = Pt(9)
        r_title.font.color.rgb = TEAL if t_idx != 2 else NAVY_DARK
        r_desc = p_it.add_run()
        r_desc.text = item[1]
        r_desc.font.name = FONT_BODY
        r_desc.font.size = Pt(8.5)
        r_desc.font.color.rgb = TEXT_DARK

print("Built Slide 10 (System Architecture)")

# ==============================================================================
# SLIDE 11 — CLI MENU FLOWCHART
# ==============================================================================
s11 = prs.slides.add_slide(blank_layout)
set_slide_background(s11, BG_LIGHT)
add_header(s11, "CLI Menu Flow: Interactive Scientific Runner", "04. Interaction Flowchart", "Detailed architectural execution flow of Main.java across its 15 scientific research workflows")
add_footer(s11, 11)

# Step Cards Flowchart (8 Steps laid out horizontally & vertically)
cli_steps = [
    {
        "step": "STEP 1",
        "title": "System Launch",
        "desc": "Main.java boots in terminal.\nDisplays scientific banner & ANSI formatting v3.0.0."
    },
    {
        "step": "STEP 2",
        "title": "Main Menu (15 Options)",
        "desc": "User surveys 15 scientific workflows (1-11 Genomics, 12 Benchmarks, 13-15 System)."
    },
    {
        "step": "STEP 3",
        "title": "Workflow Selection",
        "desc": "User selects target domain (e.g. Motif Discovery, Suffix Indexing, Flow Analysis)."
    },
    {
        "step": "STEP 4",
        "title": "Algorithm Operation",
        "desc": "Sub-menu prompts for specific DSA method (e.g. KMP, Dinic, 3-SAT Solver)."
    },
    {
        "step": "STEP 5",
        "title": "Genomic Input",
        "desc": "Load curated reference sequence (E. coli, HBB) OR enter custom DNA string."
    },
    {
        "step": "STEP 6",
        "title": "DSA Execution",
        "desc": "Core Java 17+ engine runs algorithm with nanosecond precision timer."
    },
    {
        "step": "STEP 7",
        "title": "Results & Profiler",
        "desc": "Outputs matches, matrices, cuts, along with theoretical & empirical Big-O metrics."
    },
    {
        "step": "STEP 8",
        "title": "Session Logging",
        "desc": "Records execution record to in-memory audit log; prompts to continue or exit."
    }
]

# Top Row: Steps 1 to 4
for i in range(4):
    sx = Inches(0.8 + i * 2.98)
    sy = Inches(1.4)
    step_info = cli_steps[i]
    
    card = create_card_shape(s11, sx, sy, Inches(2.78), Inches(2.35))
    
    # Step badge
    s_badge = s11.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, sx + Inches(0.12), sy + Inches(0.12), Inches(1.0), Inches(0.25))
    set_flat_style(s_badge, NAVY_DARK)
    tf_sb = s_badge.text_frame
    tf_sb.vertical_anchor = MSO_ANCHOR.MIDDLE
    psb = tf_sb.paragraphs[0]
    psb.text = step_info["step"]
    psb.font.name = FONT_HEADING
    psb.font.size = Pt(8.5)
    psb.font.bold = True
    psb.font.color.rgb = GREEN_ACCENT
    psb.alignment = PP_ALIGN.CENTER
    
    # Title
    tb_st = s11.shapes.add_textbox(sx + Inches(0.12), sy + Inches(0.42), Inches(2.54), Inches(0.38))
    tf_st = tb_st.text_frame
    tf_st.word_wrap = True
    pst = tf_st.paragraphs[0]
    pst.text = step_info["title"]
    pst.font.name = FONT_HEADING
    pst.font.size = Pt(11)
    pst.font.bold = True
    pst.font.color.rgb = TEAL
    
    # Desc
    tb_sd = s11.shapes.add_textbox(sx + Inches(0.12), sy + Inches(0.82), Inches(2.54), Inches(1.35))
    tf_sd = tb_sd.text_frame
    tf_sd.word_wrap = True
    psd = tf_sd.paragraphs[0]
    psd.text = step_info["desc"]
    psd.font.name = FONT_BODY
    psd.font.size = Pt(8.8)
    psd.font.color.rgb = TEXT_DARK
    
    # Right arrow connector between horizontal cards
    if i < 3:
        arr = s11.shapes.add_shape(MSO_SHAPE.RIGHT_ARROW, sx + Inches(2.82), sy + Inches(1.0), Inches(0.14), Inches(0.2))
        set_flat_style(arr, TEAL)

# Middle downward connector from Step 4 to Step 5
down_arr = s11.shapes.add_shape(MSO_SHAPE.DOWN_ARROW, Inches(11.0), Inches(3.82), Inches(0.25), Inches(0.22))
set_flat_style(down_arr, TEAL)

# Bottom Row: Steps 8, 7, 6, 5 (reversing to create snake loop)
bottom_order = [7, 6, 5, 4] # Corresponds to step indices 8, 7, 6, 5
for col_idx, s_idx in enumerate(bottom_order):
    sx = Inches(0.8 + col_idx * 2.98)
    sy = Inches(4.15)
    step_info = cli_steps[s_idx]
    
    card = create_card_shape(s11, sx, sy, Inches(2.78), Inches(2.4))
    
    # Step badge
    s_badge = s11.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, sx + Inches(0.12), sy + Inches(0.12), Inches(1.0), Inches(0.25))
    set_flat_style(s_badge, MINT_GREEN)
    tf_sb = s_badge.text_frame
    tf_sb.vertical_anchor = MSO_ANCHOR.MIDDLE
    psb = tf_sb.paragraphs[0]
    psb.text = step_info["step"]
    psb.font.name = FONT_HEADING
    psb.font.size = Pt(8.5)
    psb.font.bold = True
    psb.font.color.rgb = WHITE
    psb.alignment = PP_ALIGN.CENTER
    
    # Title
    tb_st = s11.shapes.add_textbox(sx + Inches(0.12), sy + Inches(0.42), Inches(2.54), Inches(0.38))
    tf_st = tb_st.text_frame
    tf_st.word_wrap = True
    pst = tf_st.paragraphs[0]
    pst.text = step_info["title"]
    pst.font.name = FONT_HEADING
    pst.font.size = Pt(11)
    pst.font.bold = True
    pst.font.color.rgb = NAVY_DARK
    
    # Desc
    tb_sd = s11.shapes.add_textbox(sx + Inches(0.12), sy + Inches(0.82), Inches(2.54), Inches(1.4))
    tf_sd = tb_sd.text_frame
    tf_sd.word_wrap = True
    psd = tf_sd.paragraphs[0]
    psd.text = step_info["desc"]
    psd.font.name = FONT_BODY
    psd.font.size = Pt(8.8)
    psd.font.color.rgb = TEXT_DARK
    
    # Left arrow connector between bottom cards
    if col_idx > 0:
        arr = s11.shapes.add_shape(MSO_SHAPE.LEFT_ARROW, sx - Inches(0.18), sy + Inches(1.0), Inches(0.14), Inches(0.2))
        set_flat_style(arr, MINT_GREEN)

# Loop back indicator
loop_tb = s11.shapes.add_textbox(Inches(0.8), Inches(6.6), Inches(11.733), Inches(0.35))
tf_loop = loop_tb.text_frame
p_loop = tf_loop.paragraphs[0]
p_loop.text = "🔄 Clean Execution Loop: Upon completion, the runner displays formatted complexity metrics and prompts the user to evaluate another workflow or exit cleanly (Option 15 / 0)."
p_loop.font.name = FONT_BODY
p_loop.font.size = Pt(9.5)
p_loop.font.color.rgb = TEXT_MUTED
p_loop.alignment = PP_ALIGN.CENTER

print("Built Slide 11 (CLI Menu Flow)")

# ==============================================================================
# SLIDE 12 — APPLICATIONS AND SIGNIFICANCE
# ==============================================================================
s12 = prs.slides.add_slide(blank_layout)
set_slide_background(s12, BG_LIGHT)
add_header(s12, "Applications and Significance: Bridging Biology & Computing", "05. Real-World Applications", "Translating abstract data structures into actionable genomic insights and clinical workflows")
add_footer(s12, 12)

# Left Column: Implemented Biological Applications in BioGenome
card_app = create_card_shape(s12, Inches(0.8), Inches(1.35), Inches(5.7), Inches(5.5))
tb_app = s12.shapes.add_textbox(Inches(1.0), Inches(1.48), Inches(5.3), Inches(5.2))
tf_app = tb_app.text_frame
tf_app.word_wrap = True

p_at = tf_app.paragraphs[0]
p_at.text = "Direct Genomic Applications in BioGenome"
p_at.font.name = FONT_HEADING
p_at.font.size = Pt(13)
p_at.font.bold = True
p_at.font.color.rgb = NAVY_DARK

apps_list = [
    ("DNA Motif Discovery: ", "Pinpointing promoter elements (TATA boxes, Pribnow box) and restriction enzyme cleavage sites (EcoRI, AluI) using linear string matching."),
    ("Allele Homology & Point Mutations: ", "Detecting single nucleotide mutations (e.g. HbA wild-type vs HbS sickle cell A→T substitution in HBB gene) using Needleman-Wunsch DP."),
    ("De Novo Contig Assembly: ", "Reconstructing fragmented next-generation sequencing reads into continuous genomic contigs via genomic bitmask shortest common superstrings."),
    ("Tandem Repeat & STR Mapping: ", "Identifying microsatellites, triplet repeat expansions (Huntington's disease CAG repeats), and duplicated domains using Kasai's LCP array."),
    ("Clinical Laboratory Logistics: ", "Maximizing daily specimen throughput, routing cold-chain logistics, and resolving researcher-to-trial conflicts using Dinic's blocking flow."),
    ("Cancer Network Monitoring: ", "Applying 2-approximation vertex covers to select minimal sensor genes (TP53, BRCA1, ATM) that monitor oncogenic signaling networks.")
]

for title, desc in apps_list:
    p = tf_app.add_paragraph()
    p.space_before = Pt(6)
    r1 = p.add_run()
    r1.text = "• " + title
    r1.font.name = FONT_HEADING
    r1.font.bold = True
    r1.font.size = Pt(9.5)
    r1.font.color.rgb = TEAL
    r2 = p.add_run()
    r2.text = desc
    r2.font.name = FONT_BODY
    r2.font.size = Pt(8.8)
    r2.font.color.rgb = TEXT_DARK

# Right Column: Academic Significance & Scope Boundaries
card_scope = create_card_shape(s12, Inches(6.8), Inches(1.35), Inches(5.733), Inches(5.5))
tb_scope = s12.shapes.add_textbox(Inches(7.0), Inches(1.48), Inches(5.3), Inches(5.2))
tf_scope = tb_scope.text_frame
tf_scope.word_wrap = True

p_st = tf_scope.paragraphs[0]
p_st.text = "Academic Significance & Scope Clarity"
p_st.font.name = FONT_HEADING
p_st.font.size = Pt(13)
p_st.font.bold = True
p_st.font.color.rgb = NAVY_DARK

scopes_list = [
    ("Educational Grounding: ", "BioGenome is engineered as a university DSA demonstration platform. It demonstrates that industry tools like BLAST, Bowtie, and BWA are built on these exact classical data structures."),
    ("Theoretical & Empirical Co-verification: ", "Every execution outputs rigorous Big-O complexity profiles (best, average, worst, and auxiliary space) contrasted with actual nanosecond execution times."),
    ("Scope Distinction (Implemented vs Real-World): ", "Directly Implemented: Exact stringology, SA-IS indexing, DP alignments, network flow, NP reductions, and streaming sampling on reference sequences.\nGeneral Bioinformatics Context: Whole-genome multi-gigabyte aligners utilize compressed Burrows-Wheeler Transform (BWT) indexes, heuristic seed-and-extend techniques, and GPU hardware acceleration."),
    ("Academic Disclaimer: ", "All genomic sequences, variant databases, and logistics graphs in BioGenome are educational models designed for algorithmic evaluation and must not be used for clinical medical diagnoses.")
]

for title, desc in scopes_list:
    p = tf_scope.add_paragraph()
    p.space_before = Pt(6)
    r1 = p.add_run()
    r1.text = "• " + title
    r1.font.name = FONT_HEADING
    r1.font.bold = True
    r1.font.size = Pt(9.5)
    r1.font.color.rgb = MINT_GREEN
    r2 = p.add_run()
    r2.text = desc
    r2.font.name = FONT_BODY
    r2.font.size = Pt(8.8)
    r2.font.color.rgb = TEXT_DARK

print("Built Slide 12 (Applications & Significance)")

# ==============================================================================
# SLIDE 13 — CONCLUSION AND FUTURE SCOPE
# ==============================================================================
s13 = prs.slides.add_slide(blank_layout)
set_slide_background(s13, BG_LIGHT)
add_header(s13, "Conclusion & Future Scope: Summary of Insights", "06. Conclusion & Roadmap", "Synthesis of project contributions, algorithmic takeaways, and planned future enhancements")
add_footer(s13, 13)

# 3 Horizontal Cards (Summary, Key Learnings, Future Scope)
c_w = Inches(3.72)
c_h = Inches(5.5)

# Card 1: Project Achievements Summary
c1 = create_card_shape(s13, Inches(0.8), Inches(1.35), c_w, c_h)
tb_c1 = s13.shapes.add_textbox(Inches(1.0), Inches(1.5), Inches(3.32), Inches(5.1))
tf_c1 = tb_c1.text_frame
tf_c1.word_wrap = True

p1 = tf_c1.paragraphs[0]
p1.text = "Project Summary"
p1.font.name = FONT_HEADING
p1.font.size = Pt(13)
p1.font.bold = True
p1.font.color.rgb = NAVY_DARK

summary_items = [
    ("6 Foundational Modules: ", "Spanning exact stringology, suffix indexing, dynamic programming, network flow, NP-completeness, and randomized/parallel algorithms."),
    ("23 Core Algorithms: ", "Faithfully implemented in clean, idiomatic Java 17+ with zero external algorithmic dependencies."),
    ("29/29 Passing Tests: ", "Automated JUnit 5 test suite confirming mathematical and functional correctness across all modules."),
    ("Dual Platform Architecture: ", "Features both an interactive 15-option scientific CLI runner and a modern standalone scientific web interface.")
]
for t, d in summary_items:
    p = tf_c1.add_paragraph()
    p.space_before = Pt(8)
    r1 = p.add_run()
    r1.text = "• " + t
    r1.font.name = FONT_HEADING
    r1.font.bold = True
    r1.font.size = Pt(9.5)
    r1.font.color.rgb = TEAL
    r2 = p.add_run()
    r2.text = d
    r2.font.name = FONT_BODY
    r2.font.size = Pt(8.8)
    r2.font.color.rgb = TEXT_DARK

# Card 2: Algorithmic Takeaways & Learnings
c2 = create_card_shape(s13, Inches(4.8), Inches(1.35), c_w, c_h)
tb_c2 = s13.shapes.add_textbox(Inches(5.0), Inches(1.5), Inches(3.32), Inches(5.1))
tf_c2 = tb_c2.text_frame
tf_c2.word_wrap = True

p2 = tf_c2.paragraphs[0]
p2.text = "Key Algorithmic Learnings"
p2.font.name = FONT_HEADING
p2.font.size = Pt(13)
p2.font.bold = True
p2.font.color.rgb = NAVY_DARK

learnings_items = [
    ("Sub-Linear Power: ", "Pre-processing chromosomes into Suffix Arrays reduces arbitrary motif lookups from O(N) to O(M log N), enabling whole-genome queries."),
    ("DP Trade-Offs: ", "Exact dynamic programming achieves optimal biological alignment but scales as O(NM), necessitating heuristic seed techniques for large genomes."),
    ("Dualities & Reductions: ", "Proving NP-completeness via Karp reductions (3-SAT → CLIQUE → IS → VC) bridges formal computer science to complex molecular biology."),
    ("Parallel Scaling: ", "Blelloch parallel scans demonstrate that work-efficient parallel tree passes compress span to O(log N) while keeping work O(N).")
]
for t, d in learnings_items:
    p = tf_c2.add_paragraph()
    p.space_before = Pt(8)
    r1 = p.add_run()
    r1.text = "• " + t
    r1.font.name = FONT_HEADING
    r1.font.bold = True
    r1.font.size = Pt(9.5)
    r1.font.color.rgb = MINT_GREEN
    r2 = p.add_run()
    r2.text = d
    r2.font.name = FONT_BODY
    r2.font.size = Pt(8.8)
    r2.font.color.rgb = TEXT_DARK

# Card 3: Realistic Future Enhancements
c3 = create_card_shape(s13, Inches(8.8), Inches(1.35), c_w, c_h)
tb_c3 = s13.shapes.add_textbox(Inches(9.0), Inches(1.5), Inches(3.32), Inches(5.1))
tf_c3 = tb_c3.text_frame
tf_c3.word_wrap = True

p3 = tf_c3.paragraphs[0]
p3.text = "Realistic Future Scope"
p3.font.name = FONT_HEADING
p3.font.size = Pt(13)
p3.font.bold = True
p3.font.color.rgb = NAVY_DARK

future_items = [
    ("FM-Index & BWT: ", "Implement Burrows-Wheeler Transform and Ferragina-Manzini index for ultra-compressed whole-human-genome indexing (< 4 GB RAM)."),
    ("Hidden Markov Models (HMM): ", "Integrate Profile HMMs (Viterbi & Forward-Backward) for ab initio gene finding and CpG island probabilistic segmentation."),
    ("GPU-Accelerated Alignments: ", "Accelerate Smith-Waterman local alignment via CUDA kernels to enable real-time screening of millions of clinical reads."),
    ("Standard VCF & BAM Ingestion: ", "Direct parsing of clinical Variant Call Format (VCF) and binary alignment maps (BAM) from Illumina and PacBio platforms.")
]
for t, d in future_items:
    p = tf_c3.add_paragraph()
    p.space_before = Pt(8)
    r1 = p.add_run()
    r1.text = "• " + t
    r1.font.name = FONT_HEADING
    r1.font.bold = True
    r1.font.size = Pt(9.5)
    r1.font.color.rgb = ACCENT_BLUE
    r2 = p.add_run()
    r2.text = d
    r2.font.name = FONT_BODY
    r2.font.size = Pt(8.8)
    r2.font.color.rgb = TEXT_DARK

print("Built Slide 13 (Conclusion and Future Scope)")

# ==============================================================================
# SAVE PRESENTATION
# ==============================================================================
output_filename = "BioGenome_DSA3_Presentation.pptx"
output_path = os.path.join(r"d:\2nd year\DSA3\BioGenome_Web", output_filename)
prs.save(output_path)
print(f"\n================================================================================")
print(f"  SUCCESSFULLY GENERATED PRESENTATION:")
print(f"  File Path: {output_path}")
print(f"  Total Slides: {len(prs.slides)}")
print(f"================================================================================")
