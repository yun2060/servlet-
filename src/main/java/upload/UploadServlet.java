package upload;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@WebServlet(name = "UploadServlet", value = "/UploadServlet")
public class UploadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    // 上传文件存储目录
    private static final String UPLOAD_DIRECTORY = "uploads";
    // 上传配置
    private static final int MEMORY_THRESHOLD   = 1024 * 1024 * 3;  // 3MB
    private static final int MAX_FILE_SIZE      = 1024 * 1024 * 40; // 40MB
    private static final int MAX_REQUEST_SIZE   = 1024 * 1024 * 50; // 50MB
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 配置上传参数
        DiskFileItemFactory factory = new DiskFileItemFactory();
        File f=new File("D:\\temp");
        if(!f.exists()){
            f.mkdirs();
        }
        // 设置内存临界值 - 超过后将产生临时文件并存储于临时目录中
        factory.setSizeThreshold(MEMORY_THRESHOLD);
        // 设置临时存储目录
        factory.setRepository(f);
        ServletFileUpload upload = new ServletFileUpload(factory);
        // 设置最大文件上传值
        upload.setFileSizeMax(MAX_FILE_SIZE);
        // 设置最大请求值 (包含文件和表单数据)
        upload.setSizeMax(MAX_REQUEST_SIZE);
        // 中文处理
        upload.setHeaderEncoding("UTF-8");
        try {
            // 解析请求的内容提取文件数据
            List<FileItem> formItems = upload.parseRequest(request);
                // 迭代表单数据
                for (FileItem item : formItems) {
                    if(item.isFormField()){
                        String name=item.getFieldName();
                        String value=item.getString("UTF-8");
                    }
                    // 处理不在表单中的字段
                    else {
                        String filename = item.getName();
                        filename=mkid()+filename;
                        String webpath="/uploads/";
                        String path=request.getSession().getServletContext().getRealPath(webpath+filename);
                        File storeFile = new File(path);
//                        // 在控制台输出文件的上传路径
                        System.out.println(path);
//                        // 保存文件到硬盘
                        item.write(storeFile);
//使用文件输入输出流保存文件
//                        File file=new File(path);
//                        file.getParentFile().mkdirs();
//                        file.createNewFile();
//                        FileOutputStream out=new FileOutputStream(file);
//                        InputStream in =item.getInputStream();
//                        byte[]buffer=new byte[1024];
//                        int len=1;
//                        while((len=in.read(buffer))!=-1){
//                            out.write(buffer,0,len);
//                        }
//                        in.close();
//                        out.close();
//                        item.delete();
                        request.setAttribute("message", "文件上传成功!");
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        // 跳转到 message.jsp
        request.getServletContext().getRequestDispatcher("/message.jsp").forward(
                request, response);
    }

public static String mkid(){
        return UUID.randomUUID().toString();
}
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }
}
