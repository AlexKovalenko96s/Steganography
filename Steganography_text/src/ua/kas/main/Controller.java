package ua.kas.main;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

import javax.imageio.ImageIO;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

public class Controller {
	@FXML
	TextArea ta_encryption;
	@FXML
	TextArea ta_decoding;

	@FXML
	ImageView iv_encryption;
	@FXML
	ImageView iv_decoding;

	@FXML
	Label l_encryption;
	@FXML
	Label l_encryptionFail;

	private String pathEncryptionSave;
	private String pathEncryptionImage;
	private String pathDecodingImage;

	public void selectEcryptionSave() {
		FileChooser fileChooser = new FileChooser();

		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
		fileChooser.getExtensionFilters().add(extFilter);

		File file = fileChooser.showSaveDialog(null);
		try {
			pathEncryptionSave = file.getAbsolutePath();
		} catch (Exception ex) {
		}
	}

	public void selectEcryptionImage() throws IOException {
		FileChooser fileChooser = new FileChooser();

		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image (*.png)", "*.png");
		fileChooser.getExtensionFilters().add(extFilter);

		File file = fileChooser.showOpenDialog(null);

		try {
			pathEncryptionImage = file.getAbsolutePath();

			File file1 = new File(pathEncryptionImage);
			Image image = new Image(file1.toURI().toString());
			iv_encryption.setImage(image);
		} catch (Exception e) {
		}
	}

	public void encryption() throws IOException {
		String text = ta_encryption.getText();
		String binary = new BigInteger(text.getBytes("UTF-8")).toString(2)
				+ new BigInteger("end".getBytes("UTF-8")).toString(2);

		System.out.println(new BigInteger(text.getBytes("UTF-8")).toString(2));

		BufferedImage image = ImageIO.read(new File(pathEncryptionImage));
		BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

		int pixel, red, green, blue, newColor;
		String temp = "";

		try {
			for (int width = 0; width < image.getWidth(); width++) {
				for (int height = 0; height < image.getHeight(); height++) {
					pixel = image.getRGB(width, height);

					red = (pixel >> 16) & 0xff;
					green = (pixel >> 8) & 0xff;
					blue = (pixel >> 0) & 0xff;

					if (binary.length() > 0) {
						temp = new BigInteger(Integer.toString(green).getBytes("UTF-8")).toString(2);

						temp = temp.substring(0, temp.length() - 1) + binary.substring(0, 1);
						binary = binary.substring(1);

						newColor = Integer.parseInt(new String(new BigInteger(temp, 2).toByteArray()));

						newImage.setRGB(width, height, new Color(red, newColor, blue).getRGB());
					} else {
						newImage.setRGB(width, height, new Color(red, green, blue).getRGB());
					}
				}
			}
			ImageIO.write(newImage, "png", new File(pathEncryptionSave));

			l_encryption.setVisible(true);
			l_encryptionFail.setVisible(false);
		} catch (Exception e) {
			l_encryption.setVisible(false);
			l_encryptionFail.setVisible(true);
		}
	}

	public void selectDecodingImage() throws IOException {
		FileChooser fileChooser = new FileChooser();

		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image (*.png)", "*.png");
		fileChooser.getExtensionFilters().add(extFilter);

		File file = fileChooser.showOpenDialog(null);

		try {
			pathDecodingImage = file.getAbsolutePath();

			File file1 = new File(pathDecodingImage);
			Image image = new Image(file1.toURI().toString());
			iv_decoding.setImage(image);
		} catch (Exception e) {
		}
	}

	public void decoding() throws IOException {
		int pixel, green;
		String temp = "", decoding = "";

		BufferedImage image = ImageIO.read(new File(pathDecodingImage));
		for (int width = 0; width < image.getWidth(); width++) {
			for (int height = 0; height < image.getHeight(); height++) {
				pixel = image.getRGB(width, height);

				green = (pixel >> 8) & 0xff;

				temp = new BigInteger(Integer.toString(green).getBytes("UTF-8")).toString(2);
				decoding += temp.substring(temp.length() - 1);
			}
		}

		decoding = decoding.substring(0, decoding.indexOf("11001010110111001100100"));

		String out = new String(new BigInteger(decoding, 2).toByteArray());

		ta_decoding.setText(out);
	}
}
