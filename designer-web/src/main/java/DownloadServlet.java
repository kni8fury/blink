import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Application;

import java.io.FileOutputStream;

//@WebServlet("/UploadDownloadFileServlet")
public class DownloadServlet extends javax.servlet.http.HttpServlet implements
javax.servlet.Servlet {
	static final long serialVersionUID = 1L;
	private static final int BUFSIZE = 4096;
	private String filePath;

	public void init() {
	}

	@Override  
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			
		doGet(request, response);
	}
	@Override  
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		String fileName = request.getParameter("fileName") + ".war";
		filePath="/Users/rpoosar/blink/"+request.getParameter("fileName")+"/"+request.getParameter("fileName")+"/target/";
		if(fileName == null || fileName.equals("")){
			throw new ServletException("File Name can't be null or empty");
		}
		File file = new File(filePath+fileName);
		if(!file.exists()){
			throw new ServletException("File doesn't exists on server.");
		}
		System.out.println("File location on server::"+file.getAbsolutePath());
		ServletOutputStream os;
		try {
			os = response.getOutputStream();

			ServletContext ctx = getServletContext();
			BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
			FileInputStream fis = new FileInputStream(filePath+fileName);
			String mimeType = ctx.getMimeType(filePath+fileName); 
			response.setContentType(mimeType != null? mimeType:"application/x-zip");
			long size=file.length();
			response.setContentLength((int)size);
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName+"\"");
			byte[] bufferData = new byte[20];
			int read=0;
			while((read = fis.read(bufferData)) > 0){
				bos.write(bufferData, 0, read);
			}  
			os.flush();


			System.out.println("File downloaded at client successfully");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}

