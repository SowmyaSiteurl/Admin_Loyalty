package siteurl.in.admin_loyalty.Adaptors;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import siteurl.in.admin_loyalty.Objects.Redeemed;
import siteurl.in.admin_loyalty.R;

/**
 * Created by siteurl on 15/11/17.
 */

public class MyRedeemCustomAdapter extends ArrayAdapter<Redeemed> {
    private ArrayList<Redeemed> originalList;
    private ArrayList<Redeemed> countryList;
    private RedeemFilter filter;
    Context mcontext;
    int positionoflist;

    public MyRedeemCustomAdapter(Context context, int textViewResourceId, ArrayList<Redeemed> countryList) {
        super(context, textViewResourceId, countryList);
        this.countryList = new ArrayList<Redeemed>();
        this.countryList.addAll(countryList);
        this.originalList = new ArrayList<Redeemed>();
        this.originalList.addAll(countryList);

        mcontext = context;
    }

    //search filter
    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new RedeemFilter();
        }
        return filter;
    }

    private class ViewHolder {
        TextView pro_name;
        TextView pro_price;
        TextView pro_exp;
        TextView pro_terms,
                pro_desc, pro_status;
        ImageView imageView, endingSoon;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        Redeemed currentenquiry = countryList.get(position);

        if (convertView == null) {
            positionoflist = position;
            convertView = LayoutInflater.from(mcontext).inflate(R.layout.myredeem, parent, false);

            holder = new ViewHolder();
            holder.pro_name = (TextView) convertView.findViewById(R.id.head_image_left_text);
            holder.pro_price = (TextView) convertView.findViewById(R.id.head_image_center_text);
            holder.pro_exp = (TextView) convertView.findViewById(R.id.head_image_right_text);
            holder.pro_terms = (TextView) convertView.findViewById(R.id.termsandconditionTV);
            holder.pro_desc = (TextView) convertView.findViewById(R.id.descriptionTV);
            // holder.pro_status = (TextView) convertView.findViewById(R.id.statusTVRedeem);
            holder.imageView = (ImageView) convertView.findViewById(R.id.head_image);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String vendorname = currentenquiry.getProduct_name();
        holder.pro_name.setText(vendorname);
        holder.pro_price.setText(currentenquiry.getPoints_value() + " points");
        holder.pro_exp.setText(currentenquiry.getExpiry_date());
        holder.pro_terms.setText(currentenquiry.getTerms_and_condition());
        holder.pro_desc.setText(currentenquiry.getProd_description());
        //  holder.pro_status.setText("status: "+currentenquiry.getStatus());

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.header);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.fitCenter();

        //It is a image loading framework
        Glide.with(getContext()).load(currentenquiry.getProduct_img())
                .thumbnail(0.5f)
                .apply(requestOptions)
                .into(holder.imageView);

        return convertView;
    }


    //search functionality
    private class RedeemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (constraint != null && constraint.toString().length() > 0) {
                ArrayList<Redeemed> filteredItems = new ArrayList<Redeemed>();

                for (int i = 0, l = originalList.size(); i < l; i++) {
                    Redeemed mycountry = originalList.get(i);
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
            countryList = (ArrayList<Redeemed>) results.values;
            notifyDataSetChanged();
            clear();
            for (int i = 0, l = countryList.size(); i < l; i++)
                add(countryList.get(i));
            notifyDataSetInvalidated();
        }
    }
}
