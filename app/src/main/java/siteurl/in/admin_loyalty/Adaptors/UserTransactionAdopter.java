package siteurl.in.admin_loyalty.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import siteurl.in.admin_loyalty.Objects.UserTransactionObject;
import siteurl.in.admin_loyalty.R;

/**
 * Created by siteurl on 15/12/17.
 */

public class UserTransactionAdopter extends ArrayAdapter<UserTransactionObject> {
    private ArrayList<UserTransactionObject> originalList;
    private ArrayList<UserTransactionObject> countryList;

    private UserTransaction filter;
    Context mcontext;
    int positionoflist;
    Calendar newDate = Calendar.getInstance();
    int mYear, mMonth, mDay;

    public UserTransactionAdopter(Context context, int textViewResourceId, ArrayList<UserTransactionObject> countryList) {
        super(context, textViewResourceId, countryList);
        this.originalList = new ArrayList<UserTransactionObject>();
        this.originalList.addAll(countryList);
        this.countryList = new ArrayList<UserTransactionObject>();
        this.countryList.addAll(countryList);
        mcontext = context;
    }

    //searchFilter
    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new UserTransaction();
        }
        return filter;
    }

    private class ViewHolder {
        TextView pro_name;
        TextView pro_des, pro_date, pro_credited, prodebited, pro_closignbalance;
        ImageView imageView;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final UserTransactionObject currentenquiry = countryList.get(position);

        if (convertView == null) {
            positionoflist = position;
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.userdata_layout1, parent, false);

            holder = new ViewHolder();
            holder.pro_name = (TextView) convertView.findViewById(R.id.Nametext);
            holder.pro_des = (TextView) convertView.findViewById(R.id.descriptiontext);
            holder.pro_date = (TextView) convertView.findViewById(R.id.datetext);
            holder.pro_credited = (TextView) convertView.findViewById(R.id.Creditedpts);
            holder.prodebited = (TextView) convertView.findViewById(R.id.debitedtext);
            holder.pro_closignbalance = (TextView) convertView.findViewById(R.id.closingtext);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String vendorname = currentenquiry.getBuyer_id();
        holder.pro_name.setText("Buyer Name :" + vendorname);
        holder.pro_des.setText("Description :" + currentenquiry.getDescription());
        holder.pro_credited.setText("Credited pts :" + currentenquiry.getCredit_points());
        holder.prodebited.setText("Debited pts :" + currentenquiry.getDebit_points());
        holder.pro_closignbalance.setText("Closing Balance :" + currentenquiry.getClosing_balance());
        String getaprovaldate = currentenquiry.getCreated_at();

        //datePicker
        String calender_date = new SimpleDateFormat("yyyy-MM-dd").format(newDate.getTime());
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "dd-MMM-yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
        Date date = null;
        String newclldate = null;
        newDate.set(mYear, mMonth, mDay);
        try {
            date = inputFormat.parse(getaprovaldate);
            newclldate = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.pro_date.setText("Date :" + newclldate);
        return convertView;
    }

    // search function
    private class UserTransaction extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (constraint != null && constraint.toString().length() > 0) {

                ArrayList<UserTransactionObject> filteredItems = new ArrayList<UserTransactionObject>();
                for (int i = 0, l = originalList.size(); i < l; i++) {
                    UserTransactionObject mycountry = originalList.get(i);
                    if (mycountry.toString().toLowerCase().contains(constraint))
                        filteredItems.add(mycountry);
                }
                result.count = filteredItems.size();
                result.values = filteredItems;

            } else {
                synchronized (this) {
                    result.values = originalList;
                    result.count = originalList.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            countryList = (ArrayList<UserTransactionObject>) results.values;
            notifyDataSetChanged();
            clear();
            for (int i = 0, l = countryList.size(); i < l; i++)
                add(countryList.get(i));
            notifyDataSetInvalidated();
        }
    }
}

