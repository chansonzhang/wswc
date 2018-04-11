package cn.edu.xjtu.hadoop;

import java.io.IOException;

import org.apache.hadoop.io.DataInputBuffer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.io.WritableUtils;

/**
 * 
 * @author zhangchen
 *
 */
public class myKeyComparator extends WritableComparator {
	protected myKeyComparator(){
		super(FileNameAndLineNumber.class,true);
	}

	@Override
	public int compare(WritableComparable key1, WritableComparable key2) {
		// TODO Auto-generated method stub
		FileNameAndLineNumber fileNameAndLineNumber1=(FileNameAndLineNumber)key1;
		FileNameAndLineNumber fileNameAndLineNumber2=(FileNameAndLineNumber)key2;
		return fileNameAndLineNumber1.compareTo(fileNameAndLineNumber2);
	}

}
