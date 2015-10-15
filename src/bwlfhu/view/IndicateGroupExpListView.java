package bwlfhu.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;

/**
 * ExpandableListView with group indicator
 */
public class IndicateGroupExpListView extends ExpandableListView implements AbsListView.OnScrollListener
{
    private FrameLayout indicatorGroup;
    private int indicatorGroupId = -1;
    private boolean indicatorGroupExpanded = false;
    private int indicatorGroupHeight;

    public IndicateGroupExpListView(Context context)
    {
        super(context);
        setOnScrollListener(this);
    }

    public IndicateGroupExpListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setOnScrollListener(this);
    }

    public IndicateGroupExpListView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        setOnScrollListener(this);
    }

    @Override
    public void setAdapter(ExpandableListAdapter adapter)
    {
        super.setAdapter(adapter);
        indicatorGroup = new FrameLayout(getContext());
        ((ViewGroup) getParent()).addView(indicatorGroup, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
        if (null == indicatorGroup)
        {
            return;
        }

        if (firstVisibleItem == AdapterView.INVALID_POSITION)
        {
            return;
        }

        long pos = getExpandableListPosition(firstVisibleItem);
        int type = getPackedPositionType(pos);
        if (type == PACKED_POSITION_TYPE_GROUP)
        {
            View groupView = getChildAt(0);
            indicatorGroupHeight = groupView.getHeight();
            indicatorGroup.setVisibility(groupView.getTop() == 0 ? View.GONE : View.VISIBLE);
        }
        else
        {
            indicatorGroup.setVisibility(View.VISIBLE);
        }

        if (indicatorGroupHeight == 0)
        {
            return;
        }

        int groupPos = getPackedPositionGroup(pos);
        boolean groupExp = isGroupExpanded(groupPos);
        if (groupPos != indicatorGroupId || indicatorGroupExpanded != groupExp)
        {
            indicatorGroupId = groupPos;
            indicatorGroupExpanded = groupExp;
            ExpandableListAdapter adapter = getExpandableListAdapter();
            View gView = adapter.getGroupView(groupPos, groupExp, indicatorGroup.getChildAt(0), null);
            if (null == indicatorGroup.getChildAt(0))
            {
                indicatorGroup.addView(gView);
            }
            indicatorGroup.setOnClickListener(new View.OnClickListener()
            {

                public void onClick(View v)
                {
                    collapseGroup(indicatorGroupId);
                }
            });
        }

        if (indicatorGroupId == AdapterView.INVALID_POSITION)
        {
            return;
        }

        int nEndPos = firstVisibleItem + 1;
        int showHeight = indicatorGroupHeight;
        long pos2 = getExpandableListPosition(nEndPos);
        int groupPos2 = getPackedPositionGroup(pos2);
        if (groupPos2 != indicatorGroupId)
        {
            View viewNext = getChildAt(1);
            showHeight = viewNext.getTop();
        }

        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) indicatorGroup.getLayoutParams();
        layoutParams.topMargin = -(indicatorGroupHeight - showHeight);
        indicatorGroup.setLayoutParams(layoutParams);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState)
    {
    }

    public void reloadGroupView(int groupPosition)
    {
        ExpandableListAdapter adapter = getExpandableListAdapter();
        if (null == adapter)
        {
            return;
        }

        int nPos = getFirstVisiblePosition();
        if (nPos == AdapterView.INVALID_POSITION)
        {
            return;
        }

        boolean isExpanded = isGroupExpanded(groupPosition);
        for (; nPos < getLastVisiblePosition(); nPos++)
        {
            long pos = getExpandableListPosition(nPos);
            int groupPos = getPackedPositionGroup(pos);
            if (groupPos == groupPosition)
            {
                int type = getPackedPositionType(pos);
                if (type == PACKED_POSITION_TYPE_GROUP)
                {
                    View groupView = getChildAt(nPos - getFirstVisiblePosition());
                    adapter.getGroupView(groupPos, isGroupExpanded(groupPos), groupView, null);
                    if (nPos != getFirstVisiblePosition())
                    {
                        break;
                    }
                }

                if (null != indicatorGroup && nPos == getFirstVisiblePosition())
                {
                    View groupView = indicatorGroup.getChildAt(0);
                    if (null != groupView)
                    {
                        adapter.getGroupView(groupPos, isExpanded, groupView, null);
                    }
                    break;
                }
            }
        }
    }
}
}
