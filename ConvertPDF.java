package Util;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ResourceBundle;
import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

/** 
 * 将文件转成PDF格式,并添加水印
 *  
 * 引入依赖，并需要安装OpenOffice
 * itext-2.1.7.jar
 * itestasian-2.1.7.jar
 * jodconverter-2.2.2.jar
 * jodconverter-cli-2.2.2.jar
 * commons-cli-1.2.jar  commons-io-1.4.jar
 * juh-3.0.1.jar jurt-3.0.1.jar  ridl-3.0.1.jar
 * slf4j-api-1.5.6.jar  slf4j-jdk14-1.5.6.jar
 * unoil-3.0.1.jar
 * xstream-1.3.1.jar
 *  
 */  
public class ConvertPDF {  

	/** 
	 * 入口方法-通过此方法转换文件至PDF格式 
	 * @param filePath  上传文件所在文件夹的绝对路径 
	 * @param fileName  文件名称 
	 * @return          生成PDF文件名 
	 * @throws FileNotFoundException 
	 * @throws DocumentException 
	 */  
	public String beginConvert(String filePath, String fileName) throws FileNotFoundException,IOException, DocumentException {  
		
		final String TXT = ".txt";
		final String DOC = ".doc";  
		final String DOCX = ".docx";  
		final String XLS = ".xls";  
		final String XLSX = ".xlsx";  
		final String PPT = ".ppt";
		final String PPTX = ".pptx";
		final String PDF = ".pdf";
		String outFile = "";  
		String fileNameOnly = "";  
		String fileExt = "";  
		if (null != fileName && fileName.lastIndexOf(".") > 0) {  
			int index = fileName.lastIndexOf(".");  
			fileNameOnly = fileName.substring(0, index);  
			fileExt = fileName.substring(index).toLowerCase();  
		}  
		String inputFile = filePath + "/" + fileName;  
		String outputFile = "";  
	
		//第一步：如果是office文档，先转为pdf文件  
		if (fileExt.equals(DOC) || fileExt.equals(DOCX) || fileExt.equals(XLS)  
				|| fileExt.equals(XLSX)||fileExt.equals(PPT)||fileExt.equals(PPTX)||fileExt.equals(TXT)) {  
			outputFile = filePath + "/" + fileNameOnly + PDF;  
			office2PDF(inputFile, outputFile);  
			inputFile = outputFile;  
			fileExt = PDF;  
		}  
		
		//第二步：把第一步生成的PDF添加自定义水印
		// 待加水印的文件  
        PdfReader reader = new PdfReader("file:/"+inputFile);  
        // 加完水印的文件  
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(inputFile));  
        // 获取总页数
        int total = reader.getNumberOfPages() + 1;    
        PdfContentByte content;  
        BaseFont base = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",BaseFont.EMBEDDED);  
        PdfGState gs = new PdfGState();  
        for (int i = 1; i < total; i++) {  
            content = stamper.getOverContent(i);// 在内容上方加水印  
            //content = stamper.getUnderContent(i);//在内容下方加水印  
            gs.setFillOpacity(0.2f);  
            // content.setGState(gs);  
            content.beginText();  
            content.setColorFill(Color.LIGHT_GRAY);  
            content.setFontAndSize(base, 50);  
            content.setTextMatrix(70, 200);  
            content.showTextAligned(Element.ALIGN_CENTER, "公司内部文件，请注意保密！", 300,350, 55);    
            content.endText();  
        }  
       //关闭流
       stamper.close();  
       //设置为只保存文件名字，以适应PDFJS框架
       	outputFile=fileNameOnly+PDF;
		outFile = outputFile;  
		return outFile;  
	}  
	

	/** 
	 * office文档转pdf文件 
	 * @param sourceFile    office文档绝对路径 
	 * @param destFile      pdf文件绝对路径 
	 * @return 
	 */  
	private int office2PDF(String sourceFile, String destFile) {  
		ResourceBundle rb = ResourceBundle.getBundle("OpenOfficeService");  
		String OpenOffice_HOME = rb.getString("OO_HOME");  
		String host_Str = rb.getString("oo_host");  
		String port_Str = rb.getString("oo_port");  
		try {  
			File inputFile = new File(sourceFile);  
			if (!inputFile.exists()) {  
				return -1; // 找不到源文件   
			}  
			// 如果目标路径不存在, 则新建该路径    
			File outputFile = new File(destFile);  
			if (!outputFile.getParentFile().exists()) {  
				outputFile.getParentFile().mkdirs();  
			}  
			// 如果从文件中读取的URL地址最后一个字符不是 '\'，则添加'\'  
            if (OpenOffice_HOME.charAt(OpenOffice_HOME.length() - 1) != '\\') {  
                OpenOffice_HOME += "\\";  
            }  
			// 启动OpenOffice的服务    
			String command = OpenOffice_HOME  
					+ "program\\soffice.exe -headless -accept=\"socket,host="  
					+ host_Str + ",port=" + port_Str + ";urp;\"";  
			System.out.println("###\n" + command);  
			Process pro = Runtime.getRuntime().exec(command);  
			// 连接openoffice服务  
			OpenOfficeConnection connection = new SocketOpenOfficeConnection(host_Str, Integer.parseInt(port_Str));  
			connection.connect();  
			// 转换   
			DocumentConverter converter = new OpenOfficeDocumentConverter(  connection);  
			
			//如果上传文档的内容是空的，内部则会出现异常 conversion failed: could not save output document;
			converter.convert(inputFile, outputFile);  

			// 关闭连接和服务  
			connection.disconnect();  
			pro.destroy();  

			return 0;  
		} catch (FileNotFoundException e) {  
			System.out.println("文件未找到！");  
			e.printStackTrace();  
			return -1;  
		} catch (Exception e) {  
			System.out.println("OpenOffice服务监听异常！");  
			e.printStackTrace();  
		}  
		return 1;  
	}  
	
	static String loadStream(BufferedInputStream in) throws IOException{  
		int ptr = 0;  
		in = new BufferedInputStream(in);  
		StringBuffer buffer = new StringBuffer();  

		while ((ptr=in.read())!= -1){  
			buffer.append((char)ptr);  
		}  
		return buffer.toString();  
	}  
	
	/**
	 * 添加水印
	 * @param inputFile 路径
	 * @throws FileNotFoundException
	 * @throws DocumentException
	 * @throws IOException
	 */
	
	public void addWater(String dirPath,String diskFileName,String waterFile) throws FileNotFoundException,DocumentException,IOException{
		//构建路径
		String dirFileUrl=dirPath+"/"+diskFileName;
		// 待加水印的文件  
        PdfReader reader = new PdfReader("file:/"+dirFileUrl);  
        // 加完水印的文件   Constants.getTempPath() + File.separator + showFileName;
       
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(waterFile));  
        // 获取总页数
        int total = reader.getNumberOfPages() + 1;    
        PdfContentByte content;  
        BaseFont base = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",BaseFont.EMBEDDED);  
        PdfGState gs = new PdfGState();  
        for (int i = 1; i < total; i++) {  
        	content = stamper.getOverContent(i);// 在内容上方加水印  
	      //content = stamper.getUnderContent(i);//在内容下方加水印  
        	gs.setFillOpacity(0.2f);
            content.beginText();
            content.setColorFill(Color.LIGHT_GRAY);  
            content.setFontAndSize(base, 32);  
            content.setTextMatrix(70, 200);  
            content.showTextAligned(Element.ALIGN_CENTER, "内部文件    请注意保密", 300,200, 35);  			             
            content.showTextAligned(Element.ALIGN_CENTER, "内部文件    请注意保密", 300,400, 35);  			             
            content.showTextAligned(Element.ALIGN_CENTER, "内部文件    请注意保密", 300,600, 35);  			             
            content.endText();  
        }  
       //关闭流
        stamper.close();  
	}

}  