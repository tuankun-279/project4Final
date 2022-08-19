package tuan.aprotrain.projectpetcare.Adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tuan.aprotrain.projectpetcare.R;
import tuan.aprotrain.projectpetcare.entity.Service;


@SuppressLint("UseSparseArrays")
public class ExpandLVCheckBox extends BaseExpandableListAdapter {

    private Context mContext;
    private ArrayList<String> mListCategory;
    private HashMap<String, List<Service>> mListService;
    private HashMap<Integer, boolean[]> mChildCheckStates;
    private ChildViewHolder childViewHolderService;

    private GroupViewHolder groupViewHolder;
    private String groupText;
    private String childText;

    public ExpandLVCheckBox(Context context, ArrayList<String> listCategory, HashMap<String, List<Service>> lisService){

        mContext = context;
        mListCategory = listCategory;
        mListService = lisService;

        mChildCheckStates = new HashMap<Integer, boolean[]>();
    }

    public int getNumberOfCheckedItemsInGroup(int mGroupPosition)
    {
        boolean getChecked[] = mChildCheckStates.get(mGroupPosition);
        int count = 0;
        if(getChecked != null) {
            for (int j = 0; j < getChecked.length; ++j) {
                if (getChecked[j] == true) count++;
            }
        }
        return  count;
    }
    public ArrayList<Service> getListCheckedChild(int mGroupPosition, String key)
    {
        ArrayList<Service> serviceArrayList = new ArrayList<>();
        boolean getChecked[] = mChildCheckStates.get(mGroupPosition);
        if(getChecked != null) {
            for (int j = 0; j < getChecked.length; ++j) {
                if (getChecked[j] == true){
                    serviceArrayList.add(mListService.get(key).get(j));
                }
            }
        }
        return  serviceArrayList;
    }

    @Override
    public int getGroupCount() {
        return mListCategory.size();
    }


    @Override
    public String getGroup(int groupPosition) {
        return mListCategory.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }


    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {


        groupText = getGroup(groupPosition);

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_item_group, null);


            groupViewHolder = new GroupViewHolder();

            groupViewHolder.mGroupText = (TextView) convertView.findViewById(R.id.petCate);

            convertView.setTag(groupViewHolder);
        } else {

            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }

        groupViewHolder.mGroupText.setText(groupText);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mListService.get(mListCategory.get(groupPosition)).size();
    }


    @Override
    public Service getChild(int groupPosition, int childPosition) {
        return mListService.get(mListCategory.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final int mGroupPosition = groupPosition;
        final int mChildPosition = childPosition;


        //childText = getChild(mGroupPosition, mChildPosition);

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_item, null);

            childViewHolderService = new ChildViewHolder();


            childViewHolderService.textViewName = (TextView) convertView
                    .findViewById(R.id.petService);
            childViewHolderService.textViewPrice = (TextView) convertView
                    .findViewById(R.id.servicePrice);
            childViewHolderService.textViewTime = (TextView) convertView
                    .findViewById(R.id.serviceTime);

            childViewHolderService.mCheckBox = (CheckBox) convertView
                    .findViewById(R.id.lstcheckBox);
            childViewHolderService.mCheckBox = (CheckBox) convertView
                    .findViewById(R.id.lstcheckBox);
            childViewHolderService.mCheckBox = (CheckBox) convertView
                    .findViewById(R.id.lstcheckBox);

            convertView.setTag(R.layout.layout_item, childViewHolderService);

        } else {

            childViewHolderService = (ChildViewHolder) convertView
                    .getTag(R.layout.layout_item);
            childViewHolderService = (ChildViewHolder) convertView
                    .getTag(R.layout.layout_item);
            childViewHolderService = (ChildViewHolder) convertView
                    .getTag(R.layout.layout_item);
        }

        childViewHolderService.textViewName.setText(mListService.get(mListCategory.get(groupPosition)).get(childPosition).getServiceName());
        childViewHolderService.textViewPrice.setText("price: "+mListService.get(mListCategory.get(groupPosition)).get(childPosition).getServicePrice()+"$");
        childViewHolderService.textViewTime.setText("time: "+mListService.get(mListCategory.get(groupPosition)).get(childPosition).getServiceTime()+" mins");


        childViewHolderService.mCheckBox.setOnCheckedChangeListener(null);


        if (mChildCheckStates.containsKey(mGroupPosition)) {

            boolean getChecked[] = mChildCheckStates.get(mGroupPosition);


            childViewHolderService.mCheckBox.setChecked(getChecked[mChildPosition]);


        } else {


            boolean getChecked[] = new boolean[getChildrenCount(mGroupPosition)];


            mChildCheckStates.put(mGroupPosition, getChecked);


            childViewHolderService.mCheckBox.setChecked(false);

        }

        childViewHolderService.mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    boolean getChecked[] = mChildCheckStates.get(mGroupPosition);
                    getChecked[mChildPosition] = isChecked;
                    mChildCheckStates.put(mGroupPosition, getChecked);

                } else {

                    boolean getChecked[] = mChildCheckStates.get(mGroupPosition);
                    getChecked[mChildPosition] = isChecked;
                    mChildCheckStates.put(mGroupPosition, getChecked);
                }
            }
        });



        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    public final class GroupViewHolder {

        TextView mGroupText;
    }

    public final class ChildViewHolder {

        TextView textViewName,textViewPrice, textViewTime;
        CheckBox mCheckBox;
    }
}