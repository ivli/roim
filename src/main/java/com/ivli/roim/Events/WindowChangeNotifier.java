
package com.ivli.roim.Events;

/**
 *
 * @author likhachev
 */

public interface WindowChangeNotifier {
    void addWindowChangeListener(WindowChangeListener aL);
    void removeWindowChangeListener(WindowChangeListener aL);    
}
