/*  +__^_________,_________,_____,________^-.-------------------,
 *  | |||||||||   `--------'     |          |                   O
 *  `+-------------USMC----------^----------|___________________|
 *    `\_,---------,---------,--------------'
 *      / X MK X /'|       /'
 *     / X MK X /  `\    /'
 *    / X MK X /`-------'
 *   / X MK X /
 *  / X MK X /
 * (________(                @author m.c.kunkel
 *  `------'
*/
package domain.utils;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.io.FileOutputStream;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jlab.groot.ui.TCanvas;

import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

public class SaveCanvas {

	public static void saveCanvas(TCanvas can) {
		String plotName = can.getTitle();
		System.out.println(plotName);
		JPanel mainPanel = makeJPanel();
		mainPanel.add(can.getCanvas());
		System.out.println(mainPanel.getWidth());
		System.out.println(can.getWidth());
		try {
			Document d = new Document(PageSize.A4.rotate());// PageSize.A4.rotate()
			PdfWriter writer = PdfWriter.getInstance(d, new FileOutputStream(plotName + ".pdf"));
			d.open();

			PdfContentByte cb = writer.getDirectContent();
			cb.saveState();
			// PageSize.A4.getWidth(), PageSize.A4.getHeight()
			PdfTemplate template = cb.createTemplate(mainPanel.getWidth(), mainPanel.getHeight());
			Graphics2D g2d = new PdfGraphics2D(cb, can.getWidth(), can.getHeight() * 75 / 100);

			g2d.scale(1.0, 0.75);
			// g2d.translate(0.0, 400.0);
			mainPanel.print(g2d);
			// frame.addNotify();
			// frame.validate();
			g2d.dispose();
			cb.addTemplate(template, 0, 0);

			cb.restoreState();
			d.close();
		} catch (Exception e) {
			//
		}
	}

	private static JPanel makeJPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setSize(GlobalConstants.SCREENWIDTH, GlobalConstants.SCREENHEIGHT);
		return mainPanel;
	}

	public static JFrame makeJFrame(String dataType) {
		String plotName = dataType + "Plots";

		JFrame frame = new JFrame(plotName);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setSize(GlobalConstants.SCREENWIDTH, GlobalConstants.SCREENHEIGHT);

		return frame;
	}

}
