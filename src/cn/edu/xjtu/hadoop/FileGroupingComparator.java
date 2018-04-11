package cn.edu.xjtu.hadoop;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class FileGroupingComparator extends WritableComparator {

	protected FileGroupingComparator() {
		super(FileNameAndLineNumber.class,true);
	}
	@Override
	public int compare(WritableComparable a, WritableComparable b) {
		FileNameAndLineNumber fileNameAndLineNumber1=(FileNameAndLineNumber)a;
		FileNameAndLineNumber fileNameAndLineNumber2=(FileNameAndLineNumber)b;
		//分组时只比较fileName
		return fileNameAndLineNumber1.getFileName().compareTo(fileNameAndLineNumber2.getFileName());
	}

}
