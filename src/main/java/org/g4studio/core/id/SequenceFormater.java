package org.g4studio.core.id;

/**
 * SequenceFormater
 * 此代码源于开源项目E3,原作者：黄云辉
 * 
 */
public interface SequenceFormater {
	public String format(long pSequence) throws FormatSequenceExcepiton;
}
