package com.wolf.wlibrary.recyclerview;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

import com.wolf.wlibrary.R;
import com.wolf.wlibrary.recyclerview.animation.AlphaInAnimation;
import com.wolf.wlibrary.recyclerview.animation.BaseAnimation;
import com.wolf.wlibrary.recyclerview.animation.ScaleInAnimation;
import com.wolf.wlibrary.recyclerview.animation.SlideInBottomAnimation;
import com.wolf.wlibrary.recyclerview.animation.SlideInLeftAnimation;
import com.wolf.wlibrary.recyclerview.animation.SlideInRightAnimation;
import com.wolf.wlibrary.recyclerview.entity.IExpandable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * <p>Description: RecyclerView的通用Adpater</p>
 *
 * @author: swolf (https://github.com/loavne)
 * @date : 2016-09-05 16:00
 */
public abstract class SwAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    /** 下一次加载 */
    private boolean mNextLoadEnable = false;
    /** 是否加载更多 */
    private boolean mLoadingMoreEnable = false;
    /** 仅第一次可能 */
    private boolean mFirstOnlyEnable = true;
    /** 打开动画 */
    private boolean mOpenAnimationEnable = false;
    /** 能否为空 */
    private boolean mEmptyEnable;
    /** 头部是否为空 */
    private boolean mHeadAndEmptyEnable;
    /** 尾部是否为空 */
    private boolean mFootAndEmptyEnable;

    private Interpolator mInterpolator = new LinearInterpolator();
    /** 持续时间 */
    private int mDuration = 300;
    /** 上一个位置 */
    private int mLastPosition = -1;
    /** 定义需求加载更多监听对象 */
    private RequestLoadMoreListener mRequestLoadMoreListener;

    /** 动画类型 */
    @AnimationType
    private BaseAnimation mCustomAnimation;

    /** 选择的动画 默认为AlphaInAnimation（淡入淡出）*/
    private BaseAnimation mSelectAnimation = new AlphaInAnimation();

    /** 头部线性布局 */
    private LinearLayout mHeaderLayout;
    /** 尾部线性布局 */
    private LinearLayout mFooterLayout;
    /** 复制头部线性布局 */
    private LinearLayout mCopyHeaderLayout = null;
    /** 复制尾部线性布局 */
    private LinearLayout mCopyFooterLayout = null;

    /** 页面数 */
    private int pageSize = -1;

    /** 内容页View */
    private View mContentView;
    /** 空页 */
    private View mEmptyView;
    /** 加载的页 */
    private View mLoadingView;
    /** 加载失败显示的页 */
    private View loadMoreFailedView;

    /** log */
    protected static final String TAG = SwAdapter.class.getSimpleName();

    protected Context mContext;
    /** 布局资源Id */
    protected int mLayoutResId;
    protected LayoutInflater mLayoutInflater;

    /** 数据源 */
    protected List<T> mData;

    /** 页面的静态常量 */
    public static final int HEADER_VIEW = 0x00000111;
    public static final int LOADING_VIEW = 0x00000222;
    public static final int FOOTER_VIEW = 0x00000333;
    public static final int EMPTY_VIEW = 0x00000555;

    @IntDef({ALPHAIN, SCALEIN, SLIDEIN_BOTTOM, SLIDEIN_LEFT, SLIDEIN_RIGHT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AnimationType {
    }

    /**
     * Use with {@link #openLoadAnimation}
     */
    public static final int ALPHAIN = 0x00000001;
    /**
     * Use with {@link #openLoadAnimation}
     */
    public static final int SCALEIN = 0x00000002;
    /**
     * Use with {@link #openLoadAnimation}
     */
    public static final int SLIDEIN_BOTTOM = 0x00000003;
    /**
     * Use with {@link #openLoadAnimation}
     */
    public static final int SLIDEIN_LEFT = 0x00000004;
    /**
     * Use with {@link #openLoadAnimation}
     */
    public static final int SLIDEIN_RIGHT = 0x00000005;

    /** 设置加载监听 */
    public void setOnLoadMoreListener(RequestLoadMoreListener requestLoadMoreListener) {
        this.mRequestLoadMoreListener = requestLoadMoreListener;
    }

    /**
     * 设置动画持续时间
     */
    public void setDuration(int duration) {
        mDuration = duration;
    }

    /**
     * 当适配器的数据源大于页面当前数量时， 加载更多功能设置为true
     * @param pageSize
     */
    public void openLoadMore(int pageSize) {
        this.pageSize = pageSize;
        mNextLoadEnable = true;

    }

    /**
     * 返回pageSize的值
     * @return
     */
    public int getPageSize() {
        return this.pageSize;
    }


    /**
     * @param layoutResId 布局资源Id.
     * @param data        数据源
     */
    public SwAdapter(int layoutResId, List<T> data) {
        this.mData = data == null ? new ArrayList<T>() : data;
        if (layoutResId != 0) {
            this.mLayoutResId = layoutResId;
        }
    }

    public SwAdapter(List<T> data) {
        this(0, data);
    }

    public SwAdapter(View contentView, List<T> data) {
        this(0, data);
        mContentView = contentView;
    }

    /**
     * 移除数据源中指定位置的Item
     *
     * @param position
     */
    public void remove(int position) {
        mData.remove(position);
        notifyItemRemoved(position + getHeaderLayoutCount());

    }

    /**
     * 插入到数据源中指定位置
     *
     * @param position
     * @param item
     */
    public void add(int position, T item) {
        mData.add(position, item);
        notifyItemInserted(position);
    }


    /**
     * 设置一个新的数据源
     *
     * @param data
     */
    public void setNewData(List<T> data) {
        this.mData = data;
        if (mRequestLoadMoreListener != null) {
            mNextLoadEnable = true;
            // mFooterLayout = null;
        }
        if (loadMoreFailedView != null) {
            removeFooterView(loadMoreFailedView);
        }
        mLastPosition = -1;
        notifyDataSetChanged();
    }

    /**
     * 添加额外数据
     *
     * @param newData
     */
    public void addData(List<T> newData) {
        this.mData.addAll(newData);
        if (mNextLoadEnable) {
            mLoadingMoreEnable = false;
        }
        notifyDataSetChanged();
    }

    /**
     * 设置加载的View
     *
     * @param loadingView
     */
    public void setLoadingView(View loadingView) {
        this.mLoadingView = loadingView;
    }

    /**
     * 获取数据源
     *
     * @return
     */
    public List<T> getData() {
        return mData;
    }

    /**
     * 获取指定item
     */
    public T getItem(int position) {
        return mData.get(position);
    }

    /**
     * if setHeadView will be return 1 if not will be return 0.
     * notice: Deprecated! Use {@link ViewGroup#getChildCount()} of {@link #getHeaderLayout()} to replace.
     *
     * @return
     */
    @Deprecated
    public int getHeaderViewsCount() {
        return mHeaderLayout == null ? 0 : 1;
    }

    /**
     * if mFooterLayout will be return 1 or not will be return 0.
     * notice: Deprecated! Use {@link ViewGroup#getChildCount()} of {@link #getFooterLayout()} to replace.
     *
     * @return
     */
    @Deprecated
    public int getFooterViewsCount() {
        return mFooterLayout == null ? 0 : 1;
    }

    /**
     * if addHeaderView will be return 1, if not will be return 0
     */
    public int getHeaderLayoutCount() {
        return mHeaderLayout == null ? 0 : 1;
    }

    /**
     * if addFooterView will be return 1, if not will be return 0
     */
    public int getFooterLayoutCount() {
        return mFooterLayout == null ? 0 : 1;
    }

    /**
     * if mEmptyView will be return 1 or not will be return 0
     *
     * @return
     */
    public int getmEmptyViewCount() {
        return mEmptyView == null ? 0 : 1;
    }

    /**
     * 返回创建的item数量
     *
     * @return
     */
    @Override
    public int getItemCount() {
        //如果加载更多则i=1,反之为0
        int i = isLoadMore() ? 1 : 0;
        //数据源的数量+头部布局数量+尾部布局数量
        int count = mData.size() + i + getHeaderLayoutCount() + getFooterLayoutCount();
        //如果数据为0，空布局不为空
        if (mData.size() == 0 && mEmptyView != null) {
            //count为0 而且头部或者尾部为false， 加载空布局
            if (count == 0 && (!mHeadAndEmptyEnable || !mFootAndEmptyEnable)) {
                count += getmEmptyViewCount();
            } else if (mHeadAndEmptyEnable || mFootAndEmptyEnable) {
                count += getmEmptyViewCount();
            }

            if ((mHeadAndEmptyEnable && getHeaderLayoutCount() == 1 && count == 1) || count == 0) {
                mEmptyEnable = true;
                count += getmEmptyViewCount();
            }

        }
        return count;
    }

    /**
     * 获取指定位置Item视图类型
     * Get the type of View that will be created by {@link #getItemView(int, ViewGroup)} for the specified item.
     *
     * @param position The position of the item within the adapter's data set whose view type we
     *                 want.
     * @return An integer representing the type of View. Two views should share the same type if one
     * can be converted to the other in {@link #getItemView(int, ViewGroup)}. Note: Integers must be in the
     * range 0 to {@link #getItemCount()} - 1.
     */
    @Override
    public int getItemViewType(int position) {
        //头部布局不为空，位置=0，返回头布局
        if (mHeaderLayout != null && position == 0) {
            return HEADER_VIEW;
        }
        /** 如果数据源为空，添加了空布局位置小于2{(headview +emptyView)} */
        if (mData.size() == 0 && mEmptyEnable && mEmptyView != null && position <= 2) {
            if ((mHeadAndEmptyEnable || mFootAndEmptyEnable) && position == 1) {
                /** 没有添加头布局 */
                if (mHeaderLayout == null && mEmptyView != null && mFooterLayout != null) {
                    return FOOTER_VIEW;
                    /** 添加了头布局 */
                } else if (mHeaderLayout != null && mEmptyView != null) {
                    return EMPTY_VIEW;
                }
            } else if (position == 0) {

                if (mHeaderLayout == null) {
                    return EMPTY_VIEW;
                } else if (mFooterLayout != null)
                    return EMPTY_VIEW;

            } else if (position == 2 && (mFootAndEmptyEnable || mHeadAndEmptyEnable) && mHeaderLayout != null && mEmptyView != null) {
                return FOOTER_VIEW;

            } else if ((!mFootAndEmptyEnable || !mHeadAndEmptyEnable) && position == 1 && mFooterLayout != null) {
                return FOOTER_VIEW;
            }
        } else if (mData.size() == 0 && mEmptyView != null && getItemCount() == (mHeadAndEmptyEnable ? 2 : 1) && mEmptyEnable) {
            return EMPTY_VIEW;
        } else if (position == mData.size() + getHeaderLayoutCount()) {
            if (mNextLoadEnable)
                return LOADING_VIEW;
            else
                return FOOTER_VIEW;
        } else if (position > mData.size() + getHeaderLayoutCount()) {
            return FOOTER_VIEW;
        }
        return getDefItemViewType(position - getHeaderLayoutCount());
    }

    /**
     * 返回指定位置item视图的类型
     * @param position
     * @return
     */
    protected int getDefItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public SwViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SwViewHolder SwViewHolder = null;
        this.mContext = parent.getContext();
        this.mLayoutInflater = LayoutInflater.from(mContext);
        switch (viewType) {
            case LOADING_VIEW:
                SwViewHolder = getLoadingView(parent);
                break;
            case HEADER_VIEW:
                SwViewHolder = new SwViewHolder(mHeaderLayout);
                break;
            case EMPTY_VIEW:
                SwViewHolder = new SwViewHolder(mEmptyView);
                break;
            case FOOTER_VIEW:
                SwViewHolder = new SwViewHolder(mFooterLayout);
                break;
            default:
                SwViewHolder = onCreateDefViewHolder(parent, viewType);
        }
        return SwViewHolder;

    }

    /**
     * 获取加载view
     * @param parent
     * @return
     */
    private SwViewHolder getLoadingView(ViewGroup parent) {
        if (mLoadingView == null) {
            return createSwViewHolder(parent, R.layout.def_loading);
        }
        return new SwViewHolder(mLoadingView);
    }

    /**
     * 当适配器创建的view（即列表项view）被窗口分离（即滑动离开了当前窗口界面）就会被调用
     *
     * @param holder
     */
    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        int type = holder.getItemViewType();
        if (type == EMPTY_VIEW || type == HEADER_VIEW || type == FOOTER_VIEW || type == LOADING_VIEW) {
            setFullSpan(holder);
        } else {
            //添加动画
            addAnimation(holder);
        }
    }

    /**
     * When set to true, the item will layout using all span area. That means, if orientation
     * is vertical, the view will have full width; if orientation is horizontal, the view will
     * have full height.
     * if the hold view use StaggeredGridLayoutManager they should using all span area
     *
     * @param holder True if this item should traverse all spans.
     */
    protected void setFullSpan(RecyclerView.ViewHolder holder) {
        if (holder.itemView.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            params.setFullSpan(true);
        }
    }

    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        //获取布局方式，（线性、表格或者瀑布流）
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int type = getItemViewType(position);
                    if (mSpanSizeLookup == null)
                        return (type == EMPTY_VIEW || type == HEADER_VIEW || type == FOOTER_VIEW || type == LOADING_VIEW) ? gridManager.getSpanCount() : 1;
                    else
                        return (type == EMPTY_VIEW || type == HEADER_VIEW || type == FOOTER_VIEW || type == LOADING_VIEW) ? gridManager.getSpanCount() : mSpanSizeLookup.getSpanSize(gridManager, position - getHeaderLayoutCount());
                }
            });
        }
        //打开加载更多
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                if (mRequestLoadMoreListener != null && pageSize == -1) {
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    int visibleItemCount = layoutManager.getChildCount();
                    openLoadMore(visibleItemCount);
                }
            }
        });

    }

    /** flag标志 */
    private boolean flag = true;
    /** */
    private SpanSizeLookup mSpanSizeLookup;

    /**
     * 空间大小查找接口
     */
    public interface SpanSizeLookup {
        int getSpanSize(GridLayoutManager gridLayoutManager, int position);
    }

    /**
     * 初始化接口
     */
    public void setSpanSizeLookup(SpanSizeLookup spanSizeLookup) {
        this.mSpanSizeLookup = spanSizeLookup;
    }

    /**
     * 绑定不同类型的holder 解决不同类型的点击事件
     *
     * @param holder
     * @param positions
     * @see #getDefItemViewType(int)
     */
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int positions) {
        int viewType = holder.getItemViewType();

        switch (viewType) {
            case 0:
                convert((SwViewHolder) holder, mData.get(holder.getLayoutPosition() - getHeaderLayoutCount()));
                break;
            case LOADING_VIEW:
                addLoadMore(holder);
                break;
            case HEADER_VIEW:
                break;
            case EMPTY_VIEW:
                break;
            case FOOTER_VIEW:
                break;
            default:
                convert((SwViewHolder) holder, mData.get(holder.getLayoutPosition() - getHeaderLayoutCount()));
                onBindDefViewHolder((SwViewHolder) holder, mData.get(holder.getLayoutPosition() - getHeaderLayoutCount()));
                break;
        }

    }

    protected SwViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
        return createSwViewHolder(parent, mLayoutResId);
    }

    /**
     * 添加布局视图
     * @param parent    父布局
     * @param layoutResId   布局资源Id
     * @return
     */
    protected SwViewHolder createSwViewHolder(ViewGroup parent, int layoutResId) {
        if (mContentView == null) {
            return new SwViewHolder(getItemView(layoutResId, parent));
        }
        return new SwViewHolder(mContentView);
    }

    /**
     * 返回头布局
     */
    public LinearLayout getHeaderLayout() {
        return mHeaderLayout;
    }

    /**
     * 返回尾布局
     */
    public LinearLayout getFooterLayout() {
        return mFooterLayout;
    }

    /**
     * 添加头布局到mHeaderLayout后面
     *
     * @param header
     */
    public void addHeaderView(View header) {
        addHeaderView(header, -1);
    }

    /**
     * @param header
     * @param index  mHeaderLayout的头节点
     *               当index = -1或者 index大于mHeaderLayout子数量时。执行添加头布局方法
     */
    public void addHeaderView(View header, int index) {
        if (mHeaderLayout == null) {
            if (mCopyHeaderLayout == null) {
                mHeaderLayout = new LinearLayout(header.getContext());
                mHeaderLayout.setOrientation(LinearLayout.VERTICAL);
                mHeaderLayout.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
                mCopyHeaderLayout = mHeaderLayout;
            } else {
                mHeaderLayout = mCopyHeaderLayout;
            }
        }
        index = index >= mHeaderLayout.getChildCount() ? -1 : index;
        mHeaderLayout.addView(header, index);
        this.notifyDataSetChanged();
    }

    /**
     * 添加尾部布局到mFooterLayout后面
     *
     * @param footer
     */
    public void addFooterView(View footer) {
        addFooterView(footer, -1);
    }

    public void addFooterView(View footer, int index) {
        mNextLoadEnable = false;
        if (mFooterLayout == null) {
            if (mCopyFooterLayout == null) {
                mFooterLayout = new LinearLayout(footer.getContext());
                mFooterLayout.setOrientation(LinearLayout.VERTICAL);
                mFooterLayout.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
                mCopyFooterLayout = mFooterLayout;
            } else {
                mFooterLayout = mCopyFooterLayout;
            }
        }
        index = index >= mFooterLayout.getChildCount() ? -1 : index;
        mFooterLayout.addView(footer, index);
        this.notifyDataSetChanged();
    }

    /**
     * 从mHeaderLayout中移除头布局，当为0时就设置为null
     *
     * @param header
     */
    public void removeHeaderView(View header) {
        if (mHeaderLayout == null) return;

        mHeaderLayout.removeView(header);
        if (mHeaderLayout.getChildCount() == 0) {
            mHeaderLayout = null;
        }
        this.notifyDataSetChanged();
    }

    public void removeFooterView(View footer) {
        if (mFooterLayout == null) return;

        mFooterLayout.removeView(footer);
        if (mFooterLayout.getChildCount() == 0) {
            mFooterLayout = null;
        }
        this.notifyDataSetChanged();
    }

    /**
     * 移除所有头布局
     */
    public void removeAllHeaderView() {
        if (mHeaderLayout == null) return;

        mHeaderLayout.removeAllViews();
        mHeaderLayout = null;
    }

    /**
     * 移除所有尾布局
     */
    public void removeAllFooterView() {
        if (mFooterLayout == null) return;

        mFooterLayout.removeAllViews();
        mFooterLayout = null;
    }

    /**
     * 设置加载失败布局
     */
    public void setLoadMoreFailedView(View view) {
        loadMoreFailedView = view;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFooterView(loadMoreFailedView);
                openLoadMore(pageSize);
            }
        });
    }

    /**
     * 加载失败时显示
     */
    public void showLoadMoreFailedView() {
        loadComplete();
        if (loadMoreFailedView == null) {
            loadMoreFailedView = mLayoutInflater.inflate(R.layout.def_load_more_failed, null);
            loadMoreFailedView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeFooterView(loadMoreFailedView);
                    openLoadMore(pageSize);
                }
            });
        }
        addFooterView(loadMoreFailedView);
    }

    public void setEmptyView(View emptyView) {
        setEmptyView(false, false, emptyView);
    }

    /**
     * @param isHeadAndEmpty false will not show headView if the data is empty true will show emptyView and headView
     * @param emptyView
     */
    public void setEmptyView(boolean isHeadAndEmpty, View emptyView) {
        setEmptyView(isHeadAndEmpty, false, emptyView);
    }

    /**
     * 设置空View如果adapter为空而且想显示头部尾部
     *
     * @param isHeadAndEmpty
     * @param isFootAndEmpty
     * @param emptyView
     */
    public void setEmptyView(boolean isHeadAndEmpty, boolean isFootAndEmpty, View emptyView) {
        mHeadAndEmptyEnable = isHeadAndEmpty;
        mFootAndEmptyEnable = isFootAndEmpty;
        mEmptyView = emptyView;
        mEmptyEnable = true;
    }

    public View getEmptyView() {
        return mEmptyView;
    }


    /**
     * 加载完成
     */
    public void loadComplete() {
        mNextLoadEnable = false;
        mLoadingMoreEnable = false;
        notifyDataSetChanged();
    }

    /**
     * 添加加载更多
     * @param holder
     */
    private void addLoadMore(RecyclerView.ViewHolder holder) {
        if (isLoadMore() && !mLoadingMoreEnable) {
            mLoadingMoreEnable = true;
            mRequestLoadMoreListener.onLoadMoreRequested();
        }
    }


    /**
     * 添加动画
     * @param holder
     */
    private void addAnimation(RecyclerView.ViewHolder holder) {
        if (mOpenAnimationEnable) {
            if (!mFirstOnlyEnable || holder.getLayoutPosition() > mLastPosition) {
                BaseAnimation animation = null;
                if (mCustomAnimation != null) {
                    animation = mCustomAnimation;
                } else {
                    animation = mSelectAnimation;
                }
                for (Animator anim : animation.getAnimators(holder.itemView)) {
                    startAnim(anim, holder.getLayoutPosition());
                }
                mLastPosition = holder.getLayoutPosition();
            }
        }
    }

    /**
     * 当加载时设置动画
     *
     * @param anim
     * @param index
     */
    protected void startAnim(Animator anim, int index) {
        anim.setDuration(mDuration).start();
        anim.setInterpolator(mInterpolator);
    }

    /**
     * 决定是否加载更多
     *
     * @return
     */
    private boolean isLoadMore() {
        return mNextLoadEnable && pageSize != -1 && mRequestLoadMoreListener != null && mData.size() >= pageSize;
    }

    /**
     * @param layoutResId ID for an XML layout resource to load
     * @param parent      Optional view to be the parent of the generated hierarchy or else simply an object that
     *                    provides a set of LayoutParams values for root of the returned
     *                    hierarchy
     * @return view will be return
     */
    protected View getItemView(int layoutResId, ViewGroup parent) {
        return mLayoutInflater.inflate(layoutResId, parent, false);
    }


    /**
     * @see #convert(SwViewHolder, Object) ()
     * @deprecated This method is deprecated
     * {@link #convert(SwViewHolder, Object)} depending on your use case.
     */
    @Deprecated
    protected void onBindDefViewHolder(SwViewHolder holder, T item) {
    }

    /**
     * 请求加载更多监听接口
     */
    public interface RequestLoadMoreListener {

        void onLoadMoreRequested();

    }


    /**
     * 设置加载动画类型
     *
     * @param animationType One of {@link #ALPHAIN}, {@link #SCALEIN}, {@link #SLIDEIN_BOTTOM}, {@link #SLIDEIN_LEFT}, {@link #SLIDEIN_RIGHT}.
     */
    public void openLoadAnimation(@AnimationType int animationType) {
        this.mOpenAnimationEnable = true;
        mCustomAnimation = null;
        switch (animationType) {
            case ALPHAIN:
                mSelectAnimation = new AlphaInAnimation();
                break;
            case SCALEIN:
                mSelectAnimation = new ScaleInAnimation();
                break;
            case SLIDEIN_BOTTOM:
                mSelectAnimation = new SlideInBottomAnimation();
                break;
            case SLIDEIN_LEFT:
                mSelectAnimation = new SlideInLeftAnimation();
                break;
            case SLIDEIN_RIGHT:
                mSelectAnimation = new SlideInRightAnimation();
                break;
            default:
                break;
        }
    }

    /**
     * 设置定制的ObjectAnimator
     *
     * @param animation ObjectAnimator
     */
    public void openLoadAnimation(BaseAnimation animation) {
        this.mOpenAnimationEnable = true;
        this.mCustomAnimation = animation;
    }

    /**
     * 当加载的时候打开动画
     */
    public void openLoadAnimation() {
        this.mOpenAnimationEnable = true;
    }

    /**
     * {@link #addAnimation(RecyclerView.ViewHolder)}
     *
     * @param firstOnly true 当第一次加载的时候显示动画  false 每次加载的是时候显示动画
     */
    public void isFirstOnly(boolean firstOnly) {
        this.mFirstOnlyEnable = firstOnly;
    }

    /**
     * 外接方法
     * @param helper A fully initialized helper.
     * @param item   The item that needs to be displayed.
     */
    protected abstract void convert(SwViewHolder helper, T item);

    @Override
    public long getItemId(int position) {
        return position;
    }

    private int recursiveExpand(int position, @NonNull List list) {
        int count = 0;
        int pos = position + list.size() - 1;
        for (int i = list.size() - 1; i >= 0; i--, pos--) {
            if (list.get(i) instanceof IExpandable) {
                IExpandable item = (IExpandable) list.get(i);
                if (item.isExpanded() && hasSubItems(item)) {
                    List subList = item.getSubItems();
                    mData.addAll(pos + 1, subList);
                    int subItemCount = recursiveExpand(pos + 1, subList);
                    count += subItemCount;
                }
            }
        }
        return count;

    }

    /**
     * Expand an expandable item
     * 展开一个可扩展的item
     *
     * @param position position of the item
     * @return the number of items that have been added.
     */
    public int expand(@IntRange(from = 0) int position) {
        T item = getItem(position);
        if (!isExpandable(item)) {
            return 0;
        }

        IExpandable expandable = (IExpandable) item;
        if (!hasSubItems(expandable)) {
            expandable.setExpanded(false);
            return 0;
        }
        int subItemCount = 0;
        if (!expandable.isExpanded()) {
            List list = expandable.getSubItems();
            mData.addAll(position + 1, list);
            subItemCount += recursiveExpand(position + 1, list);

            expandable.setExpanded(true);
            subItemCount += list.size();
        }
        notifyItemRangeInserted(position + 1, subItemCount);
        return subItemCount;
    }

    private int recursiveCollapse(@IntRange(from = 0) int position) {
        T item = getItem(position);
        if (!isExpandable(item)) {
            return 0;
        }
        IExpandable expandable = (IExpandable) item;
        int subItemCount = 0;
        if (expandable.isExpanded()) {
            List<T> subItems = expandable.getSubItems();
            for (int i = subItems.size() - 1; i >= 0; i--) {
                T subItem = subItems.get(i);
                int pos = getItemPosition(subItem);
                if (pos < 0) {
                    continue;
                }
                if (subItem instanceof IExpandable) {
                    subItemCount += recursiveCollapse(pos);
                }
                mData.remove(pos);
                subItemCount++;
            }
        }
        return subItemCount;
    }

    /**
     * Collapse an expandable item that has been expanded..
     *
     * @param position the position of the item
     * @return the number of subItems collapsed.
     */
    public int collapse(@IntRange(from = 0) int position) {
        T item = getItem(position);
        if (!isExpandable(item)) {
            return 0;
        }
        IExpandable expandable = (IExpandable) item;
        int subItemCount = recursiveCollapse(position);
        expandable.setExpanded(false);
        notifyItemRangeRemoved(position + 1, subItemCount);
        return subItemCount;
    }

    private int getItemPosition(T item) {
        return item != null && mData != null && !mData.isEmpty() ? mData.indexOf(item) : -1;
    }

    private boolean hasSubItems(IExpandable item) {
        List list = item.getSubItems();
        return list != null && list.size() > 0;
    }

    private boolean isExpandable(T item) {
        return item != null && item instanceof IExpandable;
    }
}
