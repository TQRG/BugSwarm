package org.semanticweb.owlapitools.builders;

/**
 * An interface for objects which have a modifiable collection of items.
 * 
 * @author ignazio
 * @param <B>
 *        type of builder
 * @param <R>
 *        type of item
 */
public interface SettableItem<R, B> {

    /**
     * @param item
     *        item to add
     * @return builder
     */
    B withItem(R item);
}
