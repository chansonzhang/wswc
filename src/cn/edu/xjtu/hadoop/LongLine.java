package cn.edu.xjtu.hadoop;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

/**
 * 
 * @author zhangchen
 *
 */
public class LongLine implements WritableComparable<LongLine> {
	private Text fileName;
	private Text lineNumber;
	private Text line;
	
	public LongLine() {
		this.fileName=new Text();
		this.lineNumber=new Text();
		this.line=new Text();
	}
	
	
	public LongLine(Text fileName,Text lineNumber,Text line){
		this.fileName=fileName;
		this.lineNumber= lineNumber;
		this.line= line;
	}
	

	@Override
	public void write(DataOutput out) throws IOException {
		fileName.write(out);
		lineNumber.write(out);
		line.write(out);
		
	}
	@Override
	public void readFields(DataInput in) throws IOException {
		fileName.readFields(in);
		lineNumber.readFields(in);
		line.readFields(in);
	}
	@Override
	public int compareTo(LongLine o) {
		int cmp=fileName.compareTo(o.getFileName());
		if(cmp!=0) return cmp;
		cmp=Integer.parseInt(lineNumber.toString())-Integer.parseInt(o.getLineNumber().toString());
		if(cmp!=0) return cmp;
		return line.compareTo(o.getLine());
	}
	public Text getFileName() {
		return fileName;
	}
	public void setFileName(Text fileName) {
		this.fileName = fileName;
	}
	public Text getLineNumber() {
		return lineNumber;
	}
	public void setLineNumber(Text lineNumber) {
		this.lineNumber = lineNumber;
	}
	public Text getLine() {
		return line;
	}
	public void setLine(Text line) {
		this.line = line;
	}
	
}
