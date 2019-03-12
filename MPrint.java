package laba4;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.JobAttributes;
import java.awt.JobAttributes.SidesType;
import java.awt.PageAttributes;
import java.awt.PageAttributes.OrientationRequestedType;
import java.awt.PrintJob;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.Writer;
import java.text.DateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

public class MPrint extends Writer
{
    private PrintJob job;
    private Graphics page;
    private int fontSize;
    private Font font;
    private int X, Y;
    private int width;
    private int charWidth;
    private int lineHeight;
    private int lineAscent;
    private int chars_per_line;
    private int lines_per_page;
    private int stringNum = 0, lineNum = 0;
    private int pageNum = 0;
    private boolean last_char_was_return = false;
    private static final Properties printProps = new Properties();
    private boolean draw = true;

    MPrint(String str, int fontSize, double leftMargin, double rightMargin, double topMargin, double botMargin) throws PrintCanceledException {
        final Frame frame = new Frame();
        frame.setSize(750, 750);
        final Toolkit toolkit = frame.getToolkit();
        synchronized (printProps) {
        	JobAttributes jobAttributes = new JobAttributes();
        	jobAttributes.setSides(SidesType.TWO_SIDED_LONG_EDGE);
        	PageAttributes pageAttributes = new PageAttributes();
        	pageAttributes.setOrientationRequested(OrientationRequestedType.LANDSCAPE);
        	job = toolkit.getPrintJob(frame, str, jobAttributes,pageAttributes);
        }
        if (job == null)
            throw new PrintCanceledException("User cancelled print request");
        Dimension pagesize = job.getPageDimension();
        int pagedpi = job.getPageResolution();
        if (System.getProperty("os.name").regionMatches(true, 0, "windows", 0, 7)) {
            pagedpi = toolkit.getScreenResolution();
            pagesize = new Dimension((int) (8.5 * pagedpi), 11 * pagedpi);
            fontSize = (fontSize * pagedpi) / 72;
        }
        X = (int) (leftMargin * pagedpi);
        Y = (int) (topMargin * pagedpi);
        width = pagesize.width - (int) ((leftMargin + rightMargin) * pagedpi);
        int height = pagesize.height - (int) ((topMargin + botMargin) * pagedpi);
        font = new Font("Monospaced", Font.PLAIN, fontSize);
        FontMetrics metrics = frame.getFontMetrics(font);
        lineHeight = metrics.getHeight();
        lineAscent = metrics.getAscent();
        charWidth = metrics.charWidth('0'); // Assumes a monospaced font!
        chars_per_line = (height / charWidth) - 15;
        lines_per_page = (width / lineHeight) - 6;
        Font headerFont = new Font("SansSerif", Font.ITALIC, fontSize);
        FontMetrics headerMetrics = frame.getFontMetrics(headerFont);
        int headery = (Y - (int) (0.125 * pagedpi) - headerMetrics.getHeight()) + headerMetrics.getAscent();
        final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT);
        df.setTimeZone(TimeZone.getDefault());
        String time = df.format(new Date());
        this.fontSize = fontSize;
    }

    @Override
    public void write(char[] buffer, int index, int len) {
        for (int i = index; i < (index + len); i++) {
            if (page == null)
                newpage();
            if (draw) {
                final char[] b = "Cissoid".toCharArray();
                final BufferedImage bf = new BufferedImage(600, 600,
                        BufferedImage.TYPE_INT_ARGB);
                final Graphics2D tg = bf.createGraphics();
                tg.setColor(Color.white);
                tg.fillRect(0, 0, 600, 600);
                tg.setColor(Color.BLACK);
                tg.setStroke(new MyStroke(2));
                tg.draw(new Cissoid(300,300, 150));
                page.drawImage(bf, 50, (width / 2) - 200, null);
                page.drawChars(b, 0, b.length, 125, width - 175);
                draw = false;
            }
            if (buffer[i] == '\n') {
                if (!last_char_was_return)
                    newline();
                continue;
            }
            if (buffer[i] == '\r') {
                newline();
                last_char_was_return = true;
                continue;
            }
                else
                last_char_was_return = false;
            if (Character.isWhitespace(buffer[i]) && !Character.isSpaceChar(buffer[i]) && (buffer[i] != '\t'))
                continue;
            if (stringNum >= chars_per_line) {
                newline();
                if (page == null)
                    newpage();
            }
            if (Character.isSpaceChar(buffer[i]))
                stringNum++;
            else if (buffer[i] == '\t')
                stringNum += 8 - (stringNum % 8);
            else {
                page.drawChars(buffer, i, 1, X + (stringNum * charWidth), Y
                        + (lineNum * lineHeight) + lineAscent);
                stringNum++;
            }
        }
    }

    @Override
    public void flush() { }

    @Override
    public void close() {
        if (page != null)
            page.dispose();
        job.end();
    }

    void setFontStyle() {
        final Font current = font;
        try {
            font = new Font("Monospaced", Font.BOLD | Font.ITALIC, fontSize);
        } catch (final Exception ignored) {}
        if (page != null)
            page.setFont(font);
    }

    int getCharactersPerLine()
    {
        return this.chars_per_line;
    }

    int getLinesPerPage()
    {
        return this.lines_per_page;
    }

    private void newline() {
        if (pageNum == 1)
            stringNum = 350 / charWidth;
        else
            stringNum = 0;
        lineNum++;
        if (lineNum >= lines_per_page){
            page.dispose();
            page = null;
        }
    }

    private void newpage() {
        page = job.getGraphics();
        lineNum = 0;
        stringNum = 0;
        pageNum++;
        page.setFont(font);
    }
    static class PrintCanceledException extends Exception {
		PrintCanceledException(String msg)
        {
            super(msg);
        }
    }
}
