package org.xandercat.pmdb.dao;

/**
 * Interface for all movie collection functions.  While these methods should take care of sharing updates, 
 * actual enforcement of sharing rules should be implemented by the calling service.
 * 
 * @author Scott Arnold
 */
public interface CollectionDao extends CollectionDaoCrudOps, CollectionDaoShareOps {

}
