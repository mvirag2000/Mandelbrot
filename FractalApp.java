import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 *  Modernized Mandelbrot explorer — based on Fractal3.java (circa 2001).
 *  Converted from Symantec Visual Cafe applet to standalone Swing application.
 */
public class FractalApp extends JFrame {

    private MandelbrotCanvas canvas;
    private PaletteBar paletteBar;
    private JButton cmdBack;
    private JSpinner spnMax;
    private JLabel lblScale;
    private ZoomStack stack;

    // Mouse drag state
    private int downX, downY, dragX, dragY;
    private boolean dragging = false;

    public FractalApp() {
        super("Mandelbrot Explorer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        canvas = new MandelbrotCanvas();
        canvas.topLeft = new ComplexPoint(-2, 2);
        canvas.bottomRight = new ComplexPoint(2, -2);
        canvas.max = 100;
        canvas.skew = 20;
        canvas.setPreferredSize(new Dimension(600, 450));

        stack = new ZoomStack(canvas.topLeft, canvas.bottomRight);

        paletteBar = new PaletteBar(canvas);
        paletteBar.setPreferredSize(new Dimension(600, 20));

        // --- Top toolbar ---
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        toolbar.setBackground(new Color(192, 192, 192));

        cmdBack = new JButton("Back");
        cmdBack.setEnabled(false);
        cmdBack.addActionListener(e -> goBack());
        toolbar.add(cmdBack);

        lblScale = new JLabel("  ");
        toolbar.add(lblScale);

        toolbar.add(Box.createHorizontalStrut(20));
        toolbar.add(new JLabel("Iterations:"));
        spnMax = new JSpinner(new SpinnerNumberModel(100, 50, 2000, 50));
        spnMax.addChangeListener(e -> {
            int val = (int) spnMax.getValue();
            canvas.max = val;
            paletteBar.repaint();
        });
        toolbar.add(spnMax);

        // --- Bottom palette bar with shift buttons ---
        JPanel bottomPanel = new JPanel(new BorderLayout(2, 0));
        JButton shiftLeft = new JButton("<<");
        JButton shiftRight = new JButton(">>");
        shiftLeft.addActionListener(e -> shiftPalette(10));
        shiftRight.addActionListener(e -> shiftPalette(-10));
        bottomPanel.add(shiftLeft, BorderLayout.WEST);
        bottomPanel.add(paletteBar, BorderLayout.CENTER);
        bottomPanel.add(shiftRight, BorderLayout.EAST);

        // --- Mouse interaction on canvas ---
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                downX = dragX = e.getX();
                downY = dragY = e.getY();
                dragging = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!dragging) return;
                dragging = false;
                canvas.clearRubberBand();
                int upX = e.getX(), upY = e.getY();
                // Ignore tiny drags (accidental clicks)
                if (Math.abs(upX - downX) < 4 || Math.abs(upY - downY) < 4) return;
                canvas.setScreen(downX, downY, upX, upY);
                canvas.repaint();
                stack.push(canvas.topLeft, canvas.bottomRight);
                cmdBack.setEnabled(true);
                updateScaleLabel();
            }
        });

        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (!dragging) return;
                dragX = e.getX();
                dragY = e.getY();
                canvas.setRubberBand(downX, downY, dragX, dragY);
            }
        });

        // --- Layout ---
        setLayout(new BorderLayout());
        add(toolbar, BorderLayout.NORTH);
        add(canvas, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
    }

    private void goBack() {
        stack.pop();
        canvas.topLeft = new ComplexPoint(stack.topLeft());
        canvas.bottomRight = new ComplexPoint(stack.bottomRight());
        canvas.repaint();
        updateScaleLabel();
        if (stack.isAtBottom()) cmdBack.setEnabled(false);
    }

    private void shiftPalette(int delta) {
        canvas.skew += delta;
        paletteBar.repaint();
        canvas.repaint();
    }

    private void updateScaleLabel() {
        double scale = canvas.getScaleUnit();
        lblScale.setText(String.format("Scale: %.12g", scale));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FractalApp().setVisible(true));
    }
}


/**
 *  Canvas that renders the Mandelbrot set with HSB color mapping.
 */
class MandelbrotCanvas extends JPanel {
    public ComplexPoint topLeft;
    public ComplexPoint bottomRight;
    public int max = 100;
    public int skew = 20;
    private double scaleUnit;

    // Rubber band rectangle (null when not dragging)
    private Rectangle rubberBand;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth();
        int h = getHeight();
        if (w == 0 || h == 0) return;

        double idist = topLeft.i - bottomRight.i;
        double xdist = bottomRight.x - topLeft.x;
        double planeAspect = idist / xdist;
        double screenAspect = (double) h / w;

        if (screenAspect > planeAspect)
            scaleUnit = xdist / w;
        else
            scaleUnit = idist / h;

        // Adjust coordinates to maintain aspect ratio
        topLeft.i = topLeft.i + (h * scaleUnit - idist) / 2;
        bottomRight.i = bottomRight.i - (h * scaleUnit - idist) / 2;
        topLeft.x = topLeft.x - (w * scaleUnit - xdist) / 2;
        bottomRight.x = bottomRight.x + (w * scaleUnit - xdist) / 2;

        // Render pixel by pixel using a BufferedImage for performance
        java.awt.image.BufferedImage img =
            new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_RGB);

        ComplexPoint point = new ComplexPoint();
        for (int xpos = 0; xpos < w; xpos++) {
            for (int ypos = 0; ypos < h; ypos++) {
                point.Set(topLeft.x + xpos * scaleUnit,
                          topLeft.i - ypos * scaleUnit);
                int dwell = point.MandelBrot(max);
                img.setRGB(xpos, ypos, chromaScale(dwell).getRGB());
            }
        }
        g.drawImage(img, 0, 0, null);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        // Draw rubber band overlay after everything else
        if (rubberBand != null) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Color.WHITE);
            g2.setXORMode(Color.BLACK);
            g2.drawRect(rubberBand.x, rubberBand.y,
                        rubberBand.width, rubberBand.height);
            g2.setPaintMode();
        }
    }

    public void setRubberBand(int x1, int y1, int x2, int y2) {
        int lx = Math.min(x1, x2), ly = Math.min(y1, y2);
        rubberBand = new Rectangle(lx, ly, Math.abs(x2 - x1), Math.abs(y2 - y1));
        repaint();
    }

    public void clearRubberBand() {
        rubberBand = null;
    }

    public void setScreen(int x1, int y1, int x2, int y2) {
        int leftX = Math.min(x1, x2);
        int rightX = Math.max(x1, x2);
        int topY = Math.min(y1, y2);
        int bottomY = Math.max(y1, y2);

        double saveTopX = topLeft.x;
        double saveTopI = topLeft.i;
        topLeft.x = saveTopX + scaleUnit * leftX;
        topLeft.i = saveTopI - scaleUnit * topY;
        bottomRight.x = saveTopX + scaleUnit * rightX;
        bottomRight.i = saveTopI - scaleUnit * bottomY;
    }

    public Color chromaScale(int k) {
        if (k == max)
            return Color.BLACK;
        float fk = k + skew;
        if (fk >= max) fk = max;
        if (fk < 0.00001f) fk = 0.00001f;
        return new Color(Color.HSBtoRGB((float)(max - fk) / max, 0.85f, 0.85f));
    }

    public double getScaleUnit() {
        return scaleUnit;
    }
}


/**
 *  Horizontal bar showing the current color palette.
 */
class PaletteBar extends JPanel {
    private final MandelbrotCanvas source;

    public PaletteBar(MandelbrotCanvas source) {
        this.source = source;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth();
        int h = getHeight();
        for (int x = 0; x < w; x++) {
            g.setColor(source.chromaScale(x * source.max / w));
            g.fillRect(x, 0, 1, h);
        }
    }
}


/**
 *  Simple pushdown stack for zoom history.
 */
class ZoomStack {
    private static class Frame {
        ComplexPoint topLeft, bottomRight;
        Frame next;

        Frame(ComplexPoint tl, ComplexPoint br, Frame next) {
            this.topLeft = new ComplexPoint(tl);
            this.bottomRight = new ComplexPoint(br);
            this.next = next;
        }
    }

    private Frame current;

    public ZoomStack(ComplexPoint tl, ComplexPoint br) {
        current = new Frame(tl, br, null);
    }

    public void push(ComplexPoint tl, ComplexPoint br) {
        current = new Frame(tl, br, current);
    }

    public void pop() {
        if (current.next != null) current = current.next;
    }

    public ComplexPoint topLeft() { return current.topLeft; }
    public ComplexPoint bottomRight() { return current.bottomRight; }
    public boolean isAtBottom() { return current.next == null; }
}
