package net.atomique.ksar;

import java.io.Reader;

/**
 * @author Daniel Czerwonk <d.czerwonk@googlemail.com>
 */
public interface IDataRetriever {

	/**
	 * Retrieves data from datasource
	 * @return
	 * @throws DataRetrievingFailedException
	 */
	Reader getData() throws DataRetrievingFailedException;
	
	/**
	 * Builds a string which can be used for redoing the operation
	 * @return
	 */
	String getRedoCommand();
}
