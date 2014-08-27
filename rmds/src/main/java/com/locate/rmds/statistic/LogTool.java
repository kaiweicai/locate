package com.locate.rmds.statistic;

import java.io.FileOutputStream;
import java.io.PrintWriter;

/**
 * Tool to manage console and file logging.
 */
public class LogTool
{
	String _name;
	String _nameTag;

    boolean _bLog2Screen;
    
	private PrintWriter _fileWriter = null;
    
	private String _fileName="";
	private String _statusText = null;

    /* constructor, initialize, shutdown */
	
 	public LogTool()
 	{
 	}

 	public void log2Screen()
 	{
 		_bLog2Screen = true; 		
 	}
 	
 	public void setName( String name )
 	{
 		_name = name;
 		_nameTag = _name +": ";
 	}

	public void shutdown()
	{
		if(_fileWriter != null)
		{
			_fileWriter.flush();
			_fileWriter.close();
			_fileWriter = null;
		}
	}

	public boolean setupLogfile(String fileName, String purpose ) 
	{
		if( fileName.length()!= 0 )
		{
			_fileName = fileName;
			try
			{
				FileOutputStream textFileOut = new FileOutputStream(_fileName);
				_fileWriter = new PrintWriter(textFileOut);
			}
			catch (Exception e)
			{
				_statusText = String.format("File Error! Unable to create %s %s",
											purpose, _fileName);
				return false;
			}
		}
		
		return true;
	}
	
    /* print */
	
 	public void print(String in)
	{
 		if( _bLog2Screen )
 		{
 			System.out.print( in );
 		}

		if(_fileWriter!=null)
 		{
 			_fileWriter.print(in);
 		}
	}

 	public void println(String in)
	{
 		if( _bLog2Screen )
 		{
 			System.out.println( in );
 		}

 		if(_fileWriter!=null)
 		{
 			_fileWriter.println(in);
 		}
	}

 	public void tagPrintln( String in )
	{
 		String out = _nameTag + in;
 		if( _bLog2Screen )
 		{
 			System.out.println( out );
 		}

 		if(_fileWriter!=null)
 		{
 			_fileWriter.println(out);
 		}
	}
	
	public void tagErrPrintln( String in )
	{
 		String out = _nameTag + in;
 		if( _bLog2Screen )
 		{
 			System.err.println( out );
 		}

 		if(_fileWriter!=null)
 		{
 			_fileWriter.println(out);
 		}
	}
	
	public void prefixPrintln(String tag, String in )
	{
 		String out = tag + ": " + in + "......";
 		if( _bLog2Screen )
 		{
 			System.out.println( out );
 		}

 		if(_fileWriter!=null)
 		{
 			_fileWriter.println(out);
 		}
	}
	
	public void tagPrefixPrintln( String in )
	{
 		String out = _nameTag + in + "......";
 		if( _bLog2Screen )
 		{
 			System.out.println( out );
 		}

 		if(_fileWriter!=null)
 		{
 			_fileWriter.println(out);
 		}
	}
	
    /* Helpers */
	
 	public PrintWriter getLogfileWriter()
 	{
 		return _fileWriter;
 	}
 	
 	public String getStatusText()
 	{
 		return _statusText;
 	}

}
