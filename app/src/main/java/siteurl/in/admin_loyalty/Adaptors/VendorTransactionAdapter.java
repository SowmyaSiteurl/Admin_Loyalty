package siteurl.in.admin_loyalty.Adaptors;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import siteurl.in.admin_loyalty.Objects.VendorTransaction_Object;
import siteurl.in.admin_loyalty.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by siteurl on 16/12/17.
 */

public class VendorTransactionAdapter extends ArrayAdapter<VendorTransaction_Object> {

    private ArrayList<VendorTransaction_Object> originalList;
    private ArrayList<VendorTransaction_Object> countryList;
    private VendorTransaction_Filter filter;
    int positionoflist;

    SharedPreferences loginpref;
    SharedPreferences.Editor editor;
    String userdata, sessionid, uid;
    Calendar newDate = Calendar.getInstance();
    int mYear, mMonth, mDay;

    public VendorTransactionAdapter(Context context, int textViewResourceId, ArrayList<VendorTransaction_Object> countryList) {
        super(context, textViewResourceId, countryList);
        this.originalList = new ArrayList<VendorTransaction_Object>();
        this.originalList.addAll(countryList);
        this.countryList = new ArrayList<VendorTransaction_Object>();
        this.countryList.addAll(countryList);
    }

    //search filter
    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new VendorTransaction_Filter();
        }
        return filter;
    }

    private class ViewHolder {
        TextView pro_name;
        TextView pro_des, pro_date, pro_credited, prodebited, pro_closignbalance;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //getting data from login activity
        loginpref = getContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionid = loginpref.getString("sessionid", null);
        uid = loginpref.getString("User-id", null);
        userdata = loginpref.getString("user_data", null);
        editor = loginpref.edit();

        ViewHolder holder = null;

        final VendorTransaction_Object currentenquiry = countryList.get(position);
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
        String vendorname = currentenquiry.getVendor_id();
        holder.pro_name.setText("Vendor Name:" + vendorname);
        holder.pro_des.setText("Opening Balance :" + currentenquiry.getOpening_balance());
        holder.pro_credited.setText("Closing Balance :" + currentenquiry.getClosing_balance());
        holder.prodebited.setText("Payment Amount :" + currentenquiry.getPayment_amount());
        holder.pro_closignbalance.setText("Converted Amount :" + currentenquiry.getConverted_amount_approved());
        String getaprovaldate = currentenquiry.getCreated_at();
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

    //search filter function
    private class VendorTransaction_Filter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (constraint != null && constraint.toString().length() > 0) {
                ArrayList<VendorTransaction_Object> filteredItems = new ArrayList<VendorTransaction_Object>();

                for (int i = 0, l = originalList.size(); i < l; i++) {
                    VendorTransaction_Object mycountry = originalList.get(i);
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
            countryList = (ArrayList<VendorTransaction_Object>) results.values;
            notifyDataSetChanged();
            clear();
            for (int i = 0, l = countryList.size(); i < l; i++)
                add(countryList.get(i));
            notifyDataSetInvalidated();
        }
    }
}