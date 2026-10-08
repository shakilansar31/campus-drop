"""
generate_pdf_report.py
Generates a comprehensive, easy-to-understand academic and technical PDF project report
for "CampusDrop - Smart Lost & Found Management System".
Detailed coverage of LinkedList, Stack, Queue, HashMap, OOP, and Oracle SQL Database.
"""

import os
import sys
from reportlab.lib import colors
from reportlab.lib.pagesizes import letter
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.units import inch
from reportlab.pdfgen import canvas
from reportlab.platypus import (
    SimpleDocTemplate,
    Paragraph,
    Spacer,
    Table,
    TableStyle,
    PageBreak,
    KeepTogether,
    HRFlowable
)


class NumberedCanvas(canvas.Canvas):
    """
    Two-pass canvas to dynamically compute and display 'Page X of Y'
    along with clean header and footer rules.
    """
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self._saved_page_states = []

    def showPage(self):
        self._saved_page_states.append(dict(self.__dict__))
        self._startPage()

    def save(self):
        num_pages = len(self._saved_page_states)
        for state in self._saved_page_states:
            self.__dict__.update(state)
            self.draw_page_decorations(num_pages)
            super().showPage()
        super().save()

    def draw_page_decorations(self, page_count):
        self.saveState()
        
        # Omit header and footer on Page 1 (Cover Page)
        if self._pageNumber > 1:
            # Header
            self.setFont("Helvetica-Bold", 8)
            self.setFillColor(colors.HexColor("#475569"))
            self.drawString(40, 755, "CAMPUS DROP: Smart Lost & Found Management System")
            self.setFont("Helvetica", 8)
            self.drawRightString(572, 755, "Technical & DSA Project Report")
            self.setStrokeColor(colors.HexColor("#CBD5E1"))
            self.setLineWidth(0.75)
            self.line(40, 747, 572, 747)

            # Footer
            self.line(40, 45, 572, 45)
            self.setFont("Helvetica", 8)
            self.setFillColor(colors.HexColor("#64748B"))
            self.drawString(40, 32, "Java Collections (HashMap, Queue, LinkedList, Stack) + Oracle SQL Database")
            page_text = f"Page {self._pageNumber} of {page_count}"
            self.drawRightString(572, 32, page_text)

        self.restoreState()


def create_callout(text, title=None, bg_color="#F8FAFC", border_color="#3B82F6", text_color="#1E293B"):
    """Creates a beautifully styled callout box with a colored left accent border."""
    content = []
    if title:
        title_style = ParagraphStyle(
            'CalloutTitle',
            fontName='Helvetica-Bold',
            fontSize=10,
            leading=13,
            textColor=colors.HexColor(border_color),
            spaceAfter=3
        )
        content.append(Paragraph(f"<b>{title}</b>", title_style))
    
    body_style = ParagraphStyle(
        'CalloutBody',
        fontName='Helvetica',
        fontSize=8.5,
        leading=12,
        textColor=colors.HexColor(text_color)
    )
    content.append(Paragraph(text, body_style))
    
    t = Table([[content]], colWidths=[532])
    t.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, -1), colors.HexColor(bg_color)),
        ('LINELEFT', (0, 0), (-1, -1), 3.5, colors.HexColor(border_color)),
        ('BOX', (0, 0), (-1, -1), 0.5, colors.HexColor("#E2E8F0")),
        ('TOPPADDING', (0, 0), (-1, -1), 6),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 6),
        ('LEFTPADDING', (0, 0), (-1, -1), 10),
        ('RIGHTPADDING', (0, 0), (-1, -1), 10),
    ]))
    return t


def create_code_block(code_text, label="JAVA IMPLEMENTATION"):
    """Creates a formatted code snippet box with dark syntax theme."""
    label_style = ParagraphStyle(
        'CodeLabel',
        fontName='Helvetica-Bold',
        fontSize=7.5,
        leading=9,
        textColor=colors.HexColor("#94A3B8")
    )
    code_style = ParagraphStyle(
        'CodeContent',
        fontName='Courier',
        fontSize=7.5,
        leading=10.5,
        textColor=colors.HexColor("#F8FAFC")
    )
    
    content = [
        Paragraph(f"<b>{label}</b>", label_style),
        Spacer(1, 3),
        Paragraph(code_text.replace("\n", "<br/>").replace(" ", "&nbsp;"), code_style)
    ]
    
    t = Table([[content]], colWidths=[532])
    t.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, -1), colors.HexColor("#0F172A")),
        ('BOX', (0, 0), (-1, -1), 1, colors.HexColor("#334155")),
        ('TOPPADDING', (0, 0), (-1, -1), 6),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 6),
        ('LEFTPADDING', (0, 0), (-1, -1), 10),
        ('RIGHTPADDING', (0, 0), (-1, -1), 10),
    ]))
    return t


def generate_report(output_path):
    doc = SimpleDocTemplate(
        output_path,
        pagesize=letter,
        leftMargin=40,
        rightMargin=40,
        topMargin=50,
        bottomMargin=55
    )

    styles = getSampleStyleSheet()

    # Custom typography styles
    title_style = ParagraphStyle(
        'ReportTitle',
        fontName='Helvetica-Bold',
        fontSize=24,
        leading=28,
        textColor=colors.HexColor("#0F172A"),
        alignment=1,  # Center
        spaceAfter=6
    )

    subtitle_style = ParagraphStyle(
        'ReportSubtitle',
        fontName='Helvetica-Bold',
        fontSize=12,
        leading=16,
        textColor=colors.HexColor("#2563EB"),
        alignment=1,
        spaceAfter=15
    )

    h1_style = ParagraphStyle(
        'ReportH1',
        fontName='Helvetica-Bold',
        fontSize=14,
        leading=18,
        textColor=colors.HexColor("#0F172A"),
        spaceBefore=14,
        spaceAfter=6,
        keepWithNext=True
    )

    h2_style = ParagraphStyle(
        'ReportH2',
        fontName='Helvetica-Bold',
        fontSize=11,
        leading=15,
        textColor=colors.HexColor("#1E3A8A"),
        spaceBefore=10,
        spaceAfter=4,
        keepWithNext=True
    )

    body_style = ParagraphStyle(
        'ReportBody',
        fontName='Helvetica',
        fontSize=9,
        leading=13,
        textColor=colors.HexColor("#334155"),
        spaceAfter=6
    )

    bullet_style = ParagraphStyle(
        'ReportBullet',
        fontName='Helvetica',
        fontSize=8.5,
        leading=12,
        textColor=colors.HexColor("#334155"),
        leftIndent=15,
        firstLineIndent=-10,
        spaceAfter=3
    )

    table_header_style = ParagraphStyle(
        'TableHeader',
        fontName='Helvetica-Bold',
        fontSize=8,
        leading=10,
        textColor=colors.white,
        alignment=1
    )

    table_cell_style = ParagraphStyle(
        'TableCell',
        fontName='Helvetica',
        fontSize=7.5,
        leading=10,
        textColor=colors.HexColor("#1E293B")
    )

    story = []

    # =========================================================================
    # COVER / HEADER SECTION
    # =========================================================================
    story.append(Spacer(1, 20))
    story.append(Paragraph("CAMPUS DROP", title_style))
    story.append(Paragraph("SMART LOST AND FOUND MANAGEMENT SYSTEM", subtitle_style))
    
    meta_box = create_callout(
        "<b>Core Architecture:</b> Java Data Structures (HashMap, Queue, LinkedList, Stack)<br/>"
        "<b>Database Engine:</b> Local Oracle Database 21c Enterprise / Express Edition via Oracle JDBC Driver (ojdbc8.jar)<br/>"
        "<b>Design Pattern:</b> Data Access Object (DAO) Pattern &bull; Object-Oriented Principles (OOP)<br/>"
        "<b>Presentation Layer:</b> High-Performance Pure Java HTTP Server (Port 3000) &bull; Interactive Neo-Brutalism Dashboard",
        title="ACADEMIC & TECHNICAL SPECIFICATION REPORT",
        bg_color="#FEF3C7",
        border_color="#D97706",
        text_color="#78350F"
    )
    story.append(meta_box)
    story.append(Spacer(1, 15))

    # =========================================================================
    # SECTION 1: EXECUTIVE PROJECT SUMMARY
    # =========================================================================
    story.append(Paragraph("1. Executive Project Summary", h1_style))
    story.append(HRFlowable(width="100%", thickness=1.5, color=colors.HexColor("#2563EB"), spaceAfter=8))
    
    story.append(Paragraph(
        "<b>CampusDrop</b> is a comprehensive, production-ready software system engineered to solve the persistent "
        "challenge of lost, misplaced, and found belongings across university and college campuses. "
        "Unlike basic database mockups or purely theoretical programs, CampusDrop bridges <b>classical in-memory "
        "data structures</b> with a <b>persistent relational database (Oracle SQL)</b> to deliver sub-millisecond "
        "lookups in RAM alongside durable, transactional ACID persistence on disk.",
        body_style
    ))
    story.append(Paragraph(
        "The project strictly preserves four required computer science data structures without replacing them with "
        "database queries. Instead, Oracle SQL acts as the persistent backing store, ensuring that data survives application "
        "restarts, server crashes, and terminal reboots.",
        body_style
    ))

    story.append(Spacer(1, 10))

    # =========================================================================
    # SECTION 2: DEEP-DIVE INTO THE 4 DATA STRUCTURES
    # =========================================================================
    story.append(Paragraph("2. Deep-Dive: Core Java Data Structures", h1_style))
    story.append(HRFlowable(width="100%", thickness=1.5, color=colors.HexColor("#2563EB"), spaceAfter=8))

    story.append(Paragraph(
        "Every single data structure in CampusDrop was selected deliberately to solve a specific algorithmic problem "
        "with optimal time and space complexity. Below is the comprehensive breakdown of each structure.",
        body_style
    ))

    # --- 2.1 HASHMAP ---
    story.append(Paragraph("2.1 HashMap&lt;Integer, Item&gt; — Fast O(1) Catalog & Lookup", h2_style))
    story.append(Paragraph(
        "<b>The Problem:</b> Students and administrators constantly search for items by unique ID (e.g., checking "
        "Item #104). Searching through an array or linked list requires scanning element by element ($O(N)$ time), "
        "which slows down linearly as campus inventory grows.<br/>"
        "<b>The Solution:</b> A <code>HashMap&lt;Integer, Item&gt;</code> uses the item's unique integer ID as the hash key. "
        "Through internal hash code computation and bucketing, the system computes the exact memory address in <b>constant time $O(1)$</b>.",
        body_style
    ))
    
    hm_callout = create_callout(
        "<b>Real-World Analogy:</b> Think of a campus locker facility where every locker has an exact engraved number (101, 102, 103). "
        "When an owner provides their locker key number, the security officer walks directly to that exact locker without opening "
        "any other locker. That is $O(1)$ constant time lookup.",
        title="Conceptual Analogy: The Numbered Locker System",
        bg_color="#EFF6FF",
        border_color="#2563EB",
        text_color="#1E3A8A"
    )
    story.append(hm_callout)
    story.append(Spacer(1, 6))

    hm_code = """// 1. Instantiation
private HashMap<Integer, Item> items = new HashMap<>();

// 2. Constant Time Insertion: O(1)
items.put(item.getId(), item);

// 3. Constant Time Lookup: O(1)
Item found = items.get(targetId);

// 4. Constant Time Deletion: O(1)
items.remove(targetId);"""
    story.append(create_code_block(hm_code, "HASHMAP USAGE IN CAMPUS DROP"))
    story.append(Spacer(1, 10))

    # --- 2.2 QUEUE (FIFO) ---
    story.append(Paragraph("2.2 Queue&lt;ClaimRequest&gt; — Fair FIFO Student Claim Processing", h2_style))
    story.append(Paragraph(
        "<b>The Problem:</b> When high-value items (smartphones, calculators, laptops) are reported found, multiple students "
        "often file claims asserting ownership. If claims were processed randomly, later claimants might fraudulently take an item "
        "before earlier, legitimate claimants are evaluated.<br/>"
        "<b>The Solution:</b> A <b>First-In, First-Out (FIFO) Queue</b> guarantees that student claims are queued strictly in the "
        "exact order they were submitted. The administrator evaluates the student at the head (front) of the line first.",
        body_style
    ))

    q_callout = create_callout(
        "<b>Real-World Analogy:</b> Think of a campus canteen ticket counter or bank token dispenser. The student who took "
        "Token #1 is served first. Token #2 is served next. No student can cut in line. New claimants enter at the back (rear) "
        "via <code>offer()</code>, and the front claimant is dequeued via <code>poll()</code>.",
        title="Conceptual Analogy: The Canteen Token Line",
        bg_color="#FAF5FF",
        border_color="#7C3AED",
        text_color="#581C87"
    )
    story.append(q_callout)
    story.append(Spacer(1, 6))

    q_code = """// 1. Instantiation as LinkedList implementing Queue interface
private Queue<ClaimRequest> claimQueue = new LinkedList<>();

// 2. Enqueue at Tail: O(1)
claimQueue.offer(new ClaimRequest(itemId, studentName, rollNo, proofReason));

// 3. Inspect Head without removing: O(1)
ClaimRequest frontClaim = claimQueue.peek();

// 4. Dequeue Head upon Admin Approval/Rejection: O(1)
ClaimRequest processed = claimQueue.poll();"""
    story.append(create_code_block(q_code, "QUEUE USAGE IN CAMPUS DROP"))
    story.append(Spacer(1, 10))

    # --- 2.3 LINKEDLIST ---
    story.append(Paragraph("2.3 LinkedList&lt;Item&gt; — Sequential Audit Trail of Returned Items", h2_style))
    story.append(Paragraph(
        "<b>The Problem:</b> Whenever an item is handed back to its rightful owner, an immutable, chronologically ordered "
        "record must be logged. An array would require costly resizings and copy operations as the history grows indefinitely.<br/>"
        "<b>The Solution:</b> A <b>Singly/Doubly Linked List</b> (<code>LinkedList&lt;Item&gt;</code>) links each returned item to "
        "its chronological predecessor and successor. Adding a newly returned item to the end of the chain takes <b>$O(1)$ time</b> "
        "with zero array reallocation, preserving a perfect audit history.",
        body_style
    ))

    ll_callout = create_callout(
        "<b>Real-World Analogy:</b> Think of a paper visitors' logbook at the campus security gate. Every time an item is returned, "
        "the officer writes a new line directly at the end of the ledger. Each entry connects chronologically to the previous one, "
        "forming a verifiable chain of custody.",
        title="Conceptual Analogy: The Continuous Security Ledger",
        bg_color="#ECFDF5",
        border_color="#059669",
        text_color="#064E3B"
    )
    story.append(ll_callout)
    story.append(Spacer(1, 6))

    ll_code = """// 1. Instantiation
private LinkedList<Item> recoveryHistory = new LinkedList<>();

// 2. Append to Chain upon Return: O(1)
recoveryHistory.add(returnedItem);

// 3. Sequential Traversal for Audit Report: O(N)
for (Item item : recoveryHistory) {
    System.out.println("Node: #" + item.getId() + " - " + item.getName());
}"""
    story.append(create_code_block(ll_code, "LINKEDLIST USAGE IN CAMPUS DROP"))
    story.append(Spacer(1, 10))

    # --- 2.4 STACK (LIFO) ---
    story.append(Paragraph("2.4 Stack&lt;String&gt; — LIFO Reversible Undo Operations", h2_style))
    story.append(Paragraph(
        "<b>The Problem:</b> Campus administrators or students frequently make mistakes (e.g., misreporting an item, "
        "clicking Approve accidentally, or typing wrong details). A modern system must support an instantaneous <b>Undo</b>.<br/>"
        "<b>The Solution:</b> A <b>Last-In, First-Out (LIFO) Stack</b> pushes an action description onto the top whenever a state-changing "
        "operation occurs. When the administrator clicks <b>Undo</b>, the top operation is popped immediately ($O(1)$ time) to reverse the action.",
        body_style
    ))

    stk_callout = create_callout(
        "<b>Real-World Analogy:</b> Think of a spring-loaded stack of trays in a college dining hall. The last tray placed on "
        "top of the pile is the very first tray picked up by the next student. In CampusDrop, the last action performed is "
        "the first action undone.",
        title="Conceptual Analogy: The Dining Hall Tray Dispenser",
        bg_color="#FFFBEB",
        border_color="#D97706",
        text_color="#78350F"
    )
    story.append(stk_callout)
    story.append(Spacer(1, 6))

    stk_code = """// 1. Instantiation
private Stack<String> actionStack = new Stack<>();

// 2. Push Action to Top: O(1)
actionStack.push("Reported lost item #" + id + " (" + name + ")");

// 3. Pop Most Recent Action to Undo: O(1)
if (!actionStack.isEmpty()) {
    String undoneAction = actionStack.pop();
    System.out.println("Undoing: " + undoneAction);
}"""
    story.append(create_code_block(stk_code, "STACK USAGE IN CAMPUS DROP"))
    story.append(Spacer(1, 15))

    # =========================================================================
    # SECTION 3: COMPARATIVE COMPLEXITY MATRIX
    # =========================================================================
    story.append(Paragraph("3. Algorithmic Complexity Comparison Table", h1_style))
    story.append(HRFlowable(width="100%", thickness=1.5, color=colors.HexColor("#2563EB"), spaceAfter=8))

    table_data = [
        [
            Paragraph("<b>Data Structure</b>", table_header_style),
            Paragraph("<b>Role in CampusDrop</b>", table_header_style),
            Paragraph("<b>Primary Methods</b>", table_header_style),
            Paragraph("<b>Insert Time</b>", table_header_style),
            Paragraph("<b>Lookup Time</b>", table_header_style),
            Paragraph("<b>Space</b>", table_header_style),
        ],
        [
            Paragraph("<b>HashMap&lt;K,V&gt;</b>", table_cell_style),
            Paragraph("Fast Item Catalog by ID", table_cell_style),
            Paragraph("<code>put(), get(), remove()</code>", table_cell_style),
            Paragraph("<b>O(1)</b> avg", table_cell_style),
            Paragraph("<b>O(1)</b> avg", table_cell_style),
            Paragraph("O(N)", table_cell_style),
        ],
        [
            Paragraph("<b>Queue&lt;E&gt;</b>", table_cell_style),
            Paragraph("FIFO Claim Processing", table_cell_style),
            Paragraph("<code>offer(), poll(), peek()</code>", table_cell_style),
            Paragraph("<b>O(1)</b>", table_cell_style),
            Paragraph("<b>O(1)</b> front", table_cell_style),
            Paragraph("O(N)", table_cell_style),
        ],
        [
            Paragraph("<b>LinkedList&lt;E&gt;</b>", table_cell_style),
            Paragraph("Sequential Audit Trail", table_cell_style),
            Paragraph("<code>add(), get(), iterator()</code>", table_cell_style),
            Paragraph("<b>O(1)</b> tail", table_cell_style),
            Paragraph("<b>O(N)</b> scan", table_cell_style),
            Paragraph("O(N)", table_cell_style),
        ],
        [
            Paragraph("<b>Stack&lt;E&gt;</b>", table_cell_style),
            Paragraph("LIFO Reversible Undo", table_cell_style),
            Paragraph("<code>push(), pop(), peek()</code>", table_cell_style),
            Paragraph("<b>O(1)</b>", table_cell_style),
            Paragraph("<b>O(1)</b> top", table_cell_style),
            Paragraph("O(N)", table_cell_style),
        ]
    ]

    t_matrix = Table(table_data, colWidths=[80, 120, 110, 70, 80, 72])
    t_matrix.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), colors.HexColor("#0F172A")),
        ('GRID', (0, 0), (-1, -1), 0.5, colors.HexColor("#CBD5E1")),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.white, colors.HexColor("#F8FAFC")]),
        ('TOPPADDING', (0, 0), (-1, -1), 4),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 4),
        ('LEFTPADDING', (0, 0), (-1, -1), 5),
        ('RIGHTPADDING', (0, 0), (-1, -1), 5),
        ('ALIGN', (3, 1), (5, -1), 'CENTER'),
    ]))
    story.append(t_matrix)
    story.append(Spacer(1, 15))

    # =========================================================================
    # SECTION 4: PERSISTENT ORACLE DATABASE INTEGRATION
    # =========================================================================
    story.append(Paragraph("4. Persistent Oracle SQL Database Layer", h1_style))
    story.append(HRFlowable(width="100%", thickness=1.5, color=colors.HexColor("#2563EB"), spaceAfter=8))

    story.append(Paragraph(
        "CampusDrop connects to a local <b>Oracle Database 21c (XE)</b> instance via the official <b>Oracle JDBC "
        "Driver (<code>ojdbc8.jar</code>)</b>. All modifications in the Java collections are synchronized with "
        "relational tables using PreparedStatements and two-phase ACID transactions.",
        body_style
    ))

    story.append(Paragraph("<b>4.1 Database Architecture & Schema:</b>", h2_style))
    story.append(Paragraph(
        "&bull; <b><code>ITEMS</code> Table:</b> Stores reported items with fields <code>ITEM_ID</code> (PK), "
        "<code>ITEM_NAME</code>, <code>CATEGORY</code>, <code>LOCATION</code>, <code>REPORT_DATE</code>, "
        "<code>REPORTED_BY</code>, <code>ITEM_TYPE</code> (CHECK: LOST/FOUND), and <code>STATUS</code> (CHECK: ACTIVE/CLAIMED/RETURNED).<br/>"
        "&bull; <b><code>CLAIMS</code> Table:</b> Stores student claims with foreign key <code>ITEM_ID</code> references, "
        "student roll numbers, reasons, and claim statuses (PENDING/APPROVED/REJECTED).<br/>"
        "&bull; <b><code>RECOVERY_HISTORY</code> Table:</b> Stores permanent recovery logs with recovery timestamps "
        "and staff handler signatures.",
        bullet_style
    ))

    story.append(Spacer(1, 4))
    story.append(Paragraph("<b>4.2 ACID Two-Phase Commit Transaction for Item Returns:</b>", h2_style))
    story.append(Paragraph(
        "When an item is returned, the system must perform two atomic operations simultaneously: (1) update the status "
        "in <code>ITEMS</code> to 'RETURNED', and (2) insert a record into <code>RECOVERY_HISTORY</code>. "
        "If either fails, the transaction is rolled back completely to prevent data corruption:",
        body_style
    ))

    tx_code = """try (Connection conn = DatabaseConnection.getConnection()) {
    conn.setAutoCommit(false); // 1. Begin JDBC Transaction
    try {
        itemDAO.updateItemStatus(itemId, "RETURNED");
        historyDAO.addRecoveryHistory(item, staffName);
        conn.commit();         // 2. Commit both atomically
    } catch (Exception ex) {
        conn.rollback();       // 3. Rollback on any failure
        throw ex;
    }
}"""
    story.append(create_code_block(tx_code, "JDBC TRANSACTION (ACID GUARANTEE)"))
    story.append(Spacer(1, 15))

    # =========================================================================
    # SECTION 5: OOP ARCHITECTURE
    # =========================================================================
    story.append(Paragraph("5. Object-Oriented Programming (OOP) Pillars", h1_style))
    story.append(HRFlowable(width="100%", thickness=1.5, color=colors.HexColor("#2563EB"), spaceAfter=8))

    oop_data = [
        [
            Paragraph("<b>OOP Pillar</b>", table_header_style),
            Paragraph("<b>CampusDrop Implementation</b>", table_header_style),
            Paragraph("<b>Engineering Benefit</b>", table_header_style)
        ],
        [
            Paragraph("<b>Encapsulation</b>", table_cell_style),
            Paragraph("Model classes (<code>Item.java</code>, <code>ClaimRequest.java</code>) declare private fields with strictly validated public getters and setters.", table_cell_style),
            Paragraph("Prevents unauthorized modification of item IDs, status transitions, and timestamps.", table_cell_style)
        ],
        [
            Paragraph("<b>Abstraction</b>", table_cell_style),
            Paragraph("DAO classes (<code>ItemDAO</code>, <code>ClaimDAO</code>, <code>HistoryDAO</code>) hide low-level SQL strings, connection pools, and result set parsers.", table_cell_style),
            Paragraph("Business logic classes interact solely with clean Java objects without writing raw SQL.", table_cell_style)
        ],
        [
            Paragraph("<b>Polymorphism</b>", table_cell_style),
            Paragraph("Interface segregation: <code>Queue&lt;ClaimRequest&gt; = new LinkedList&lt;&gt;()</code>. The interface can be swapped for a PriorityQueue with zero code changes.", table_cell_style),
            Paragraph("Loose coupling and high maintainability.", table_cell_style)
        ],
        [
            Paragraph("<b>Class & Object</b>", table_cell_style),
            Paragraph("Clear separation of concerns: Presentation (<code>WebServer</code>), Domain (<code>CampusDrop</code>), Persistence (<code>DAOs</code>), Infrastructure (<code>DatabaseConnection</code>).", table_cell_style),
            Paragraph("Modular, testable, and maintainable codebase.", table_cell_style)
        ]
    ]

    t_oop = Table(oop_data, colWidths=[90, 242, 200])
    t_oop.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), colors.HexColor("#1E3A8A")),
        ('GRID', (0, 0), (-1, -1), 0.5, colors.HexColor("#CBD5E1")),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.white, colors.HexColor("#F8FAFC")]),
        ('TOPPADDING', (0, 0), (-1, -1), 4),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 4),
        ('LEFTPADDING', (0, 0), (-1, -1), 6),
        ('RIGHTPADDING', (0, 0), (-1, -1), 6),
    ]))
    story.append(t_oop)
    story.append(Spacer(1, 15))

    # =========================================================================
    # SECTION 6: VIVA VOCE QUESTIONS & ANSWERS
    # =========================================================================
    story.append(Paragraph("6. Viva Voce & Evaluation Questions & Answers", h1_style))
    story.append(HRFlowable(width="100%", thickness=1.5, color=colors.HexColor("#2563EB"), spaceAfter=8))

    vivas = [
        ("Q1: Why did you use HashMap instead of a traditional Array or ArrayList for item lookup?",
         "<b>Answer:</b> An Array or ArrayList requires an O(N) sequential search to locate an item by its ID. "
         "If the campus reports 10,000 items, searching takes up to 10,000 comparisons. A HashMap provides "
         "O(1) constant-time lookup by hashing the ID key directly to its bucket, ensuring instantaneous access regardless of size."),

        ("Q2: Why is Queue the correct data structure for student claim requests?",
         "<b>Answer:</b> Multiple students may claim the same found item. To prevent bias or fraud, claims must be processed "
         "in strict First-In, First-Out (FIFO) sequence. A Queue guarantees that earlier claimants are verified before subsequent ones."),

        ("Q3: Why not store recovery history in an ArrayList instead of LinkedList?",
         "<b>Answer:</b> An ArrayList requires contiguous memory and incurs costly O(N) resizing and element copying when full. "
         "A LinkedList dynamically allocates independent nodes on the heap and inserts new returned items at the tail in O(1) time "
         "without any memory reallocation overhead."),

        ("Q4: How does the Stack data structure implement the Undo feature?",
         "<b>Answer:</b> Every user action (reporting an item, approving a claim, returning an item) is pushed to the top of an "
         "action stack in O(1) time. When Undo is requested, the stack pops the top element (Last-In, First-Out), allowing the system "
         "to reverse the most recent operation first."),

        ("Q5: If you have Oracle Database, why keep the Java Data Structures in memory?",
         "<b>Answer:</b> In-memory data structures provide microsecond response times for dashboard rendering, search filtering, and match detection. "
         "Oracle SQL provides persistent, crash-resilient ACID storage on disk. Combining both gives the application the speed of RAM and the durability of SQL.")
    ]

    for q, a in vivas:
        v_box = create_callout(
            f"<b>{q}</b><br/><br/>{a}",
            title="EXAMINER QUESTION & PREPARED ANSWER",
            bg_color="#F8FAFC",
            border_color="#0F172A",
            text_color="#1E293B"
        )
        story.append(v_box)
        story.append(Spacer(1, 6))

    # =========================================================================
    # SECTION 7: HOW TO COMPILE AND RUN
    # =========================================================================
    story.append(Spacer(1, 10))
    story.append(Paragraph("7. Execution Instructions (PowerShell)", h1_style))
    story.append(HRFlowable(width="100%", thickness=1.5, color=colors.HexColor("#2563EB"), spaceAfter=8))

    run_instructions = """# 1. Navigate to Project Directory
cd "C:\\Users\\shaki\\OneDrive\\Desktop\\CAMPUS DROP"

# 2. Compile with Oracle JDBC Classpath
javac -cp "lib/ojdbc8.jar;src" src/model/*.java src/database/*.java src/*.java

# 3. Launch Web Server (Dashboard at http://localhost:3000)
java -cp "lib/ojdbc8.jar;src" WebServer

# 4. Launch Console Terminal Menu (Options 1-13)
java -cp "lib/ojdbc8.jar;src" Main"""

    story.append(create_code_block(run_instructions, "POWERSHELL COMMANDS"))

    # Build the document
    doc.build(story, canvasmaker=NumberedCanvas)
    print(f"Report successfully generated at: {output_path}")


if __name__ == "__main__":
    out_dir = r"C:\Users\shaki\OneDrive\Desktop\CAMPUS DROP"
    out_file = os.path.join(out_dir, "CampusDrop_Project_Report.pdf")
    generate_report(out_file)
