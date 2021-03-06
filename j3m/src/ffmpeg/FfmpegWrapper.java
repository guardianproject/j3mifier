package ffmpeg;

import java.io.File;
import java.util.Arrays;

import util.StreamTo;
import util.StreamToStdOut;
import util.StreamToString;
import util.Util;

import framework.FrameworkProperties;


public class FfmpegWrapper {


	public void createStill(File inputFile, File outputFile) throws FfmpegException {
		FrameworkProperties config = FrameworkProperties.getInstance();
		runCommand(inputFile,outputFile,config.getffmpegCreateStill());
	}

	public void extractMetadata(File inputFile, File outputFile) throws FfmpegException {
		FrameworkProperties config = FrameworkProperties.getInstance();
		runCommand(inputFile,outputFile,config.getFfmpegGetAttachment());
	}


	public void changeResolution(File inputFile, File outputFile, String width, String height) throws FfmpegException {
		FrameworkProperties config = FrameworkProperties.getInstance();
		runCommand(inputFile,outputFile,Util.replaceWidthHeight(config.getffmpegChangeResolution(), width, height));
	}

	public void changeFormat(File inputFile, File outputFile) throws FfmpegException  {
		FrameworkProperties config = FrameworkProperties.getInstance();
		runCommand(inputFile,outputFile,config.getffmpegChangeFormat());
	}


	/**
	 * Needs to have the input file in mp4 format
	 * @param inputFile
	 * @return
	 * @throws FfmpegException 
	 */
	public void convertToOgv(File inputFile) throws FfmpegException{
		if ("mp4".equals(Util.getFileExtenssion(inputFile.getName()))){
			FrameworkProperties config = FrameworkProperties.getInstance();
			runCommand(inputFile,inputFile,config.getffmpeg2Theora());
		}else {
			throw new FfmpegException(inputFile + " not in mp4 format");
		}

	}
	
	private int runCommand(File inputFile, File outputFile, String command) throws FfmpegException {
		StreamToStdOut streamProcessor = new StreamToStdOut();
		return runCommand(inputFile, outputFile, command, streamProcessor);
	}
	private int runCommand(File inputFile, File outputFile, String command, StreamTo processor) throws FfmpegException {
		try {
			//figure out the file name
			command = Util.replaceFileMarkers(command, inputFile.getAbsolutePath(), outputFile.getAbsolutePath());
			ProcessBuilder processBuilder = new ProcessBuilder(Arrays.asList(command.split(" ")));
			processBuilder.redirectErrorStream(true);
			Process p = processBuilder.start();
			processor.setStream(p.getInputStream());
			new Thread(processor, "output stream").start();
			p.waitFor(); 

            int exitValue = p.exitValue();
            return exitValue;
			
		} catch (Exception e) {
			throw new FfmpegException("Could not run the ffmpeg command: " + command, e);
		} 
		
	}
	
	public String getVideoRgbMd5(File inputFile) throws FfmpegException   {
		FrameworkProperties config = FrameworkProperties.getInstance();
		StreamToString streamProcessor = new StreamToString("([.]*)MD5=([^ \\.]*)(\\n)");
		runCommand(inputFile,inputFile,config.getffmpegVideoRgbMd5(),streamProcessor);
		return streamProcessor.getResult();
	}
	
	public String getAudioMd5(File inputFile) throws FfmpegException   {
		FrameworkProperties config = FrameworkProperties.getInstance();
		StreamToString streamProcessor = new StreamToString("([.]*)MD5=([^ \\.]*)(\\n)");
		runCommand(inputFile,inputFile,config.getffmpegAudioMd5(),streamProcessor);
		return streamProcessor.getResult();
	}
}
