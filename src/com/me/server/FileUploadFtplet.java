package com.me.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;

import org.apache.ftpserver.ftplet.DefaultFtpReply;
import org.apache.ftpserver.ftplet.DefaultFtplet;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.ftplet.FtpSession;
import org.apache.ftpserver.ftplet.FtpletResult;

import com.me.db.DataOperate;

/**
* @author <a href="http://mina.apache.org">Apache MINA Project</a>*
*/
public class FileUploadFtplet extends DefaultFtplet {

	public String file = "CategoryInfos.data";
	/**
	 * 文件上传完后校验
	 */
    @Override
    public FtpletResult onUploadEnd(FtpSession session, FtpRequest request)
            throws FtpException, IOException {
    	//上传文件名
    	String uploadFileName = request.getArgument();
    	DataOperate operate = new DataOperate();
    	//服务器地址
    	InetSocketAddress inetSocketAddress = session.getServerAddress();
    	//上传的文件是数据文件
    	if(file.equals(uploadFileName)) {
    		//删除24小时前未上传的图片
    		operate.deleteRecord();
    		FtpFile ftpFile = session.getFileSystemView().getFile(request.getArgument());
    		InputStream fileStream = ftpFile.createInputStream(0);
    		//同一个图片会有多行（栏目和版块不一样）
    		operate.batchInserRecord(fileStream, inetSocketAddress.getHostString());
    		fileStream.close();
    		ftpFile.delete();
    		System.out.println("删除数据文件；"+ftpFile.getAbsolutePath());
    	} else {
    		FtpFile fileWork = session.getFileSystemView().getWorkingDirectory();
    		String fileWorkName = fileWork.getAbsolutePath();
    		
    		String uploadFileRelativePath = fileWorkName + "/" + uploadFileName;
    		if(uploadFileRelativePath.endsWith("/")) {
    			uploadFileRelativePath = fileWorkName + uploadFileName;
    		}
    		if(!uploadFileRelativePath.startsWith("/")) {
    			uploadFileRelativePath = "/"+uploadFileRelativePath;
    		}
//			上传一个文件，更新记录状态
			operate.updateRecord(uploadFileRelativePath, inetSocketAddress.getHostString());
			System.out.println("更新记录状态:"+uploadFileRelativePath);
    	}
    	 return super.onUploadEnd(session, request);
    }
    /**
     * 文件上传前校验
     */
    @Override
    public FtpletResult onUploadStart(FtpSession session, FtpRequest request)
            throws FtpException, IOException {
		FtpFile fileWork = session.getFileSystemView().getWorkingDirectory();
		String fileWorkName = fileWork.getAbsolutePath();
		String uploadFileName = request.getArgument();
		if(file.equals(uploadFileName)) {
			return super.onUploadStart(session, request);
		}
		String uploadFileRelativePath = fileWorkName + "/" + uploadFileName;
		if(fileWorkName.endsWith("/")) {
			uploadFileRelativePath = fileWorkName + uploadFileName;
		}
		if(!uploadFileRelativePath.startsWith("/")) {
			uploadFileRelativePath = "/"+uploadFileRelativePath;
		}
		DataOperate operate = new DataOperate();
		
		String isload = operate.queryUploadedRecord(uploadFileRelativePath);
//		System.out.println("查询是否已经上传："+uploadFileRelativePath);
		String command = request.getCommand();
		if("STOR".equals(command)) {
			//如果已经上传该文件，则直接跳过
			if("1".equals(isload)) {
//				System.out.println("跳过，已经上传："+uploadFileRelativePath);
				session.write(new DefaultFtpReply(FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN, "file existed"));
				session.write(new DefaultFtpReply(FtpReply.REPLY_425_CANT_OPEN_DATA_CONNECTION, "退出"));
				return FtpletResult.SKIP;
			} else if(isload == null) {
//				System.out.println("未上传数据文件："+uploadFileRelativePath);
				session.write(new DefaultFtpReply(FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN, "file 未包含栏目，版块信息"));
				session.write(new DefaultFtpReply(FtpReply.REPLY_425_CANT_OPEN_DATA_CONNECTION, "退出"));
				return FtpletResult.SKIP;
			}
		}
		return super.onUploadStart(session, request);
		
    }
    
}
