package ANN1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import javafx.application.Application;
import javafx.stage.*;

public class ReadFile extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage stage) throws Exception {
		FileChooser fc = new FileChooser();
		String path = getClass().getResource("").getPath();// /bin/package/
		fc.setInitialDirectory(new File(path));
		FileReader fr;
		BufferedReader br;
		File file;
		
		
		Properties properties = new Properties();
		String configFile = "config.properties";
		try {
		    properties.load(new FileInputStream(getClass().getResource(configFile).getPath()));
		} catch (FileNotFoundException ex) {
		    ex.printStackTrace();
		} catch (IOException ex) {
		    ex.printStackTrace();
		}
		
		// 第二個參數為預設值，如果沒取到值的時候回傳預設值
		int paraSize = Integer.parseInt(properties.getProperty("inputParameter"));
		String str[];
		Double data[];
		
		System.out.println((double) 2 / 3);
		
		
		
		file = fc.showOpenDialog(stage);
		if (file != null) {
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			
			while (br.ready()) {
				str = br.readLine().trim().split("\\s+|,");
				paraSize = str.length;
				data = new Double[paraSize];
				for (int i = 0; i < paraSize; i++) {
					data[i] = Double.parseDouble(str[i]);
					System.out.print(data[i]+" ");
				}
				System.out.println();
			}
			
		}
		
		
		
	}

}
