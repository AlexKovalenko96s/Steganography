package ua.kas.main;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

public class Controller {
	@FXML
	ImageView iv_encryption;
	@FXML
	ImageView iv_decoding;
	@FXML
	ImageView iv_encryptionText;
	@FXML
	ImageView iv_decodingText;

	@FXML
	Button btn_saveEncryption;
	@FXML
	Button btn_saveDecoding;
	@FXML
	Button btn_loadEncryption;
	@FXML
	Button btn_loadDecoding;
	@FXML
	Button btn_loadMessage;

	private String pathEncryptionSave;
	private String pathEncryptionImage;
	private String pathDecodingSave;
	private String pathDecodingImage;
	private String pathMessage;

	public void selectSave(ActionEvent e) {
		FileChooser fileChooser = new FileChooser();

		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
		fileChooser.getExtensionFilters().add(extFilter);

		File file = fileChooser.showSaveDialog(null);
		try {
			if (e.getSource() == btn_saveEncryption)
				pathEncryptionSave = file.getAbsolutePath();
			else if (e.getSource() == btn_saveDecoding)
				pathDecodingSave = file.getAbsolutePath();
		} catch (Exception ex) {
		}
	}

	public void selectImage(ActionEvent e) throws IOException {
		FileChooser fileChooser = new FileChooser();

		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image (*.png)", "*.png");
		fileChooser.getExtensionFilters().add(extFilter);

		File file = fileChooser.showOpenDialog(null);

		try {
			if (e.getSource() == btn_loadEncryption) {
				pathEncryptionImage = file.getAbsolutePath();
				File file1 = new File(pathEncryptionImage);
				Image image = new Image(file1.toURI().toString());
				iv_encryption.setImage(image);
			} else if (e.getSource() == btn_loadDecoding) {
				pathDecodingImage = file.getAbsolutePath();
				File file1 = new File(pathDecodingImage);
				Image image = new Image(file1.toURI().toString());
				iv_decoding.setImage(image);
			} else if (e.getSource() == btn_loadMessage) {
				pathMessage = file.getAbsolutePath();
				File file1 = new File(pathMessage);
				Image image = new Image(file1.toURI().toString());
				iv_encryptionText.setImage(image);
			}
		} catch (Exception ex) {
		}
	}

	public void encryption() throws IOException {
		BufferedImage message = ImageIO.read(new File(pathMessage));
		BufferedImage image = ImageIO.read(new File(pathEncryptionImage));
		BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

		int pixel, red, green, blue, newColorR, newColorG;
		String color = "", tempR = "", tempG = "";

		try {
			for (int width = 0; width < message.getWidth(); width++) {
				for (int height = 0; height < message.getHeight(); height++) {
					pixel = message.getRGB(width, height);

					red = (pixel >> 16) & 0xff;
					green = (pixel >> 8) & 0xff;
					blue = (pixel >> 0) & 0xff;

					if (red > 99)
						color += red;
					else if (red < 100 && red > 9)
						color += "0" + red;
					else if (red < 10)
						color += "00" + red;

					if (green > 99)
						color += green;
					else if (green < 100 && green > 9)
						color += "0" + green;
					else if (green < 10)
						color += "00" + green;

					if (blue > 99)
						color += blue;
					else if (blue < 100 && blue > 9)
						color += "0" + blue;
					else if (blue < 10)
						color += "00" + blue;
				}
			}
		} catch (Exception e) {
		}

		color = "11111111111111111111" + new BigInteger(color.getBytes("UTF-8")).toString(2) + "11111111111111111111";

		try {
			for (int width = 0; width < image.getWidth(); width++) {
				for (int height = 0; height < image.getHeight(); height++) {
					pixel = image.getRGB(width, height);

					red = (pixel >> 16) & 0xff;
					green = (pixel >> 8) & 0xff;
					blue = (pixel >> 0) & 0xff;

					if (color.length() > 1) {
						tempR = new BigInteger(Integer.toString(red).getBytes("UTF-8")).toString(2);
						tempG = new BigInteger(Integer.toString(green).getBytes("UTF-8")).toString(2);

						tempR = tempR.substring(0, tempR.length() - 1) + color.substring(0, 1);
						tempG = tempG.substring(0, tempG.length() - 1) + color.substring(1, 2);
						color = color.substring(2);

						newColorR = Integer.parseInt(new String(new BigInteger(tempR, 2).toByteArray()));
						newColorG = Integer.parseInt(new String(new BigInteger(tempG, 2).toByteArray()));

						newImage.setRGB(width, height, new Color(newColorR, newColorG, blue).getRGB());
					} else if (color.length() == 1) {
						tempR = new BigInteger(Integer.toString(red).getBytes("UTF-8")).toString(2);

						tempR = tempR.substring(0, tempR.length() - 1) + color.substring(0, 1);
						color = color.substring(1);

						newColorR = Integer.parseInt(new String(new BigInteger(tempR, 2).toByteArray()));

						newImage.setRGB(width, height, new Color(newColorR, green, blue).getRGB());
					} else
						newImage.setRGB(width, height, new Color(red, green, blue).getRGB());
				}
			}
			ImageIO.write(newImage, "png", new File(pathEncryptionSave));
		} catch (Exception e) {
		}
	}

	public void decoding() throws IOException {
		int pixel, red, green;
		String tempR = "", tempG = "", decoding = "";

		BufferedImage image = ImageIO.read(new File(pathDecodingImage));
		for (int width = 0; width < image.getWidth(); width++) {
			for (int height = 0; height < image.getHeight(); height++) {
				pixel = image.getRGB(width, height);

				red = (pixel >> 16) & 0xff;
				green = (pixel >> 8) & 0xff;

				tempR = new BigInteger(Integer.toString(red).getBytes("UTF-8")).toString(2);
				decoding += tempR.substring(tempR.length() - 1);

				tempG = new BigInteger(Integer.toString(green).getBytes("UTF-8")).toString(2);
				decoding += tempG.substring(tempG.length() - 1);
			}
		}
		System.out.println(decoding.substring(decoding.indexOf("11111111111111111111") + 20,
				decoding.lastIndexOf("11111111111111111111")));

		decoding = decoding.substring(decoding.indexOf("11111111111111111111") + 20);
		decoding = decoding.substring(0, decoding.indexOf("11111111111111111111"));

		String message = new String(new BigInteger(decoding, 2).toByteArray());

		ArrayList<Integer> colorList = new ArrayList<>();

		for (int i = 0; i < message.length() / 3; i++) {
			colorList.add(Integer.parseInt(message.substring(i * 3, i * 3 + 3)));
		}

		BufferedImage newImage = new BufferedImage(colorList.size() / 3, 1, BufferedImage.TYPE_INT_ARGB);

		for (int i = 0; i < newImage.getWidth(); i++) {
			newImage.setRGB(i, 0,
					new Color(colorList.get(i * 3), colorList.get(i * 3 + 1), colorList.get(i * 3 + 2)).getRGB());
		}

		Image card = SwingFXUtils.toFXImage(newImage, null);
		iv_decodingText.setImage(card);

		ImageIO.write(newImage, "png", new File(pathDecodingSave));
	}
}
