package com.oilpalm3f.mainapp.uihelper.expandablecheckbox.listeners;

//Not Using
public interface OnChildrenCheckStateChangedListener {

  /**
   * @param firstChildFlattenedIndex the flat position of the first child in the {@link
   * CheckedExpandableGroup}
   * @param numChildren the total number of children in the {@link CheckedExpandableGroup}
   */
  void updateChildrenCheckState(int firstChildFlattenedIndex, int numChildren);
}
