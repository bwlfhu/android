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

        int nPos = view.pointToPosition(0, 0);
        if (nPos == AdapterView.INVALID_POSITION)
        {
            return;
        }

        long pos = getExpandableListPosition(nPos);
        int childPos = ExpandableListView.getPackedPositionChild(pos);// 获取第一行child的id
        if (childPos == AdapterView.INVALID_POSITION)
        {
            // 第一行不是显示child,就是group,此时没必要显示指示器
            View groupView = getChildAt(nPos - getFirstVisiblePosition());
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

        int groupPos = ExpandableListView.getPackedPositionGroup(pos);// 获取第一行group的id
        boolean groupExp = isGroupExpanded(groupPos);
        if (groupPos != indicatorGroupId || indicatorGroupExpanded != groupExp)
        {
            // 如果指示器显示的不是当前group
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

        // calculate point (0,indicatorGroupHeight) 下面是形成往上推出的效果
        int showHeight = indicatorGroupHeight;
        int nEndPos = pointToPosition(0, indicatorGroupHeight);// 第二个item的位置
        if (nEndPos == AdapterView.INVALID_POSITION)
        {
            return;
        }

        long pos2 = getExpandableListPosition(nEndPos);
        int groupPos2 = ExpandableListView.getPackedPositionGroup(pos2);// 获取第二个group的id
        if (groupPos2 != indicatorGroupId)
        {
            View viewNext = getChildAt(nEndPos - getFirstVisiblePosition());
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
}
