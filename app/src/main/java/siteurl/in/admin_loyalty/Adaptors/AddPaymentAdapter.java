package siteurl.in.admin_loyalty.Adaptors;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.TaskCompletionSource;

import java.util.ArrayList;

import siteurl.in.admin_loyalty.CompletePayments;
import siteurl.in.admin_loyalty.Objects.Venders_Product;
import siteurl.in.admin_loyalty.Objects.Vendors;
import siteurl.in.admin_loyalty.R;

/**
 * Created by siteurl on 14/12/17.
 */

public class AddPaymentAdapter extends ArrayAdapter<Vendors> {

    private ArrayList<Vendors> originalList;
    private CountryFilter filter;
    int positionoflist;

    public AddPaymentAdapter(Context context, int textViewResourceId, ArrayList<Vendors> countryList) {
        super(context, textViewResourceId, countryList);
        this.originalList = new ArrayList<Vendors>();
        this.originalList.addAll(countryList);
    }

    //searchFilter
    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new CountryFilter();
        }
        return filter;
    }

    private class ViewHolder {
        TextView vndr_name;
        ImageView nextpage;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        final Vendors currentenquiry = originalList.get(position);
        if (convertView == null) {
            positionoflist = position;

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.addpaymentforvendor_layout, parent, false);
            holder = new ViewHolder();
            holder.vndr_name = (TextView) convertView.findViewById(R.id.opening);
            holder.nextpage = (ImageView) convertView.findViewById(R.id.myimg);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String vendorname = currentenquiry.getName();
        holder.vndr_name.setText(vendorname.substring(0, 1).toUpperCase() + vendorname.substring(1));

        holder.nextpage.setOnClickListener(new View.OnClickListener() {
            String user_id = String.valueOf(currentenquiry.getVnderuser_id());

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), CompletePayments.class);
                intent.putExtra("saveid", user_id);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            }
        });

        return convertView;
    }

    //search Filter Function
    private class CountryFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (constraint != null && constraint.toString().length() > 0) {
                ArrayList<Vendors> filteredItems = new ArrayList<Vendors>();

                for (int i = 0, l = originalList.size(); i < l; i++) {
                    Vendors country = originalList.get(i);
                    if (country.toString().toLowerCase().contains(constraint))
                        filteredItems.add(country);
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
            originalList = (ArrayList<Vendors>) results.values;
            notifyDataSetChanged();
            clear();
            for (int i = 0, l = originalList.size(); i < l; i++)
                add(originalList.get(i));
            notifyDataSetInvalidated();
        }
    }
}
