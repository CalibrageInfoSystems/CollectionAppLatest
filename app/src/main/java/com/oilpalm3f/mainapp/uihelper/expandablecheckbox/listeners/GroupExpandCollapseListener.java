package com.oilpalm3f.mainapp.uihelper.expandablecheckbox.listeners;

import com.oilpalm3f.mainapp.uihelper.expandablecheckbox.models.ExpandableGroup;

//Not Using
public interface GroupExpandCollapseListener {

  /**
   * Called when a group is expanded
   * @param group the {@link ExpandableGroup} being expanded
   */
  void onGroupExpanded(ExpandableGroup group);

  /**
   * Called when a group is collapsed
   * @param group the {@link ExpandableGroup} being collapsed
   */
  void onGroupCollapsed(ExpandableGroup group);
}
