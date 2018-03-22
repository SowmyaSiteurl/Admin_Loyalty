package siteurl.in.admin_loyalty.Adaptors;

/**
 * Created by siteurl on 23/10/17.
 */

import android.content.Context;
import android.content.Intent;
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

import java.util.ArrayList;

import siteurl.in.admin_loyalty.CalltoVendor;
import siteurl.in.admin_loyalty.Objects.Vendors;
import siteurl.in.admin_loyalty.R;


public class MyCustomAdapter extends ArrayAdapter<Vendors> {

    private ArrayList<Vendors> originalList;
    private ArrayList<Vendors> countryList;
    private CountryFilter filter;

    ArrayList<String> phonenumberlist = new ArrayList<>();
    ArrayList<String> namelist = new ArrayList<>();
    ArrayList<String> emailidlist = new ArrayList<>();
    ArrayList<String> enquiryidlist = new ArrayList<>();

    String phonenumber, name, emailid, enquiryid;
    int positionoflist;

    public MyCustomAdapter(Context context, int textViewResourceId, ArrayList<Vendors> countryList) {
        super(context, textViewResourceId, countryList);
        this.countryList = new ArrayList<Vendors>();
        this.countryList.addAll(countryList);
        this.originalList = new ArrayList<Vendors>();
        this.originalList.addAll(countryList);
    }

    //search filter
    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new CountryFilter();
        }
        return filter;
    }

    private class ViewHolder {
        TextView vndr_name;
        TextView vndr_email;
        TextView vnder_address;
        TextView vndr_id;
        ImageView callvnder, vnderlogo;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        final Vendors currentenquiry = countryList.get(position);
        if (convertView == null) {

            positionoflist = position;
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.country_info, parent, false);
            phonenumber = currentenquiry.getPhone();
            phonenumberlist.add(phonenumber);

            emailid = currentenquiry.getEmail();
            emailidlist.add(emailid);

            name = currentenquiry.getName();
            namelist.add(name);

            enquiryid = currentenquiry.getVnderuser_id();
            enquiryidlist.add(enquiryid);

            holder = new ViewHolder();
            holder.vndr_name = (TextView) convertView.findViewById(R.id.content_name_view);
            holder.vndr_email = (TextView) convertView.findViewById(R.id.textView);
            holder.vnder_address = (TextView) convertView.findViewById(R.id.textView1);
            holder.vndr_id = (TextView) convertView.findViewById(R.id.content_avatar_title1);

            holder.callvnder = (ImageView) convertView.findViewById(R.id.overflow1);
            holder.vnderlogo = (ImageView) convertView.findViewById(R.id.vndr_logoview);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String vendorname = currentenquiry.getName();
        holder.vndr_name.setText("Name: " + vendorname.substring(0, 1).toUpperCase() + vendorname.substring(1));
        holder.vndr_email.setText("Email: " + currentenquiry.getEmail());
        holder.vnder_address.setText("Address: " + currentenquiry.getAddress());
        holder.vndr_id.setText("Phone: " + currentenquiry.getPhone());

        /*holder.nextpage.setOnClickListener(new View.OnClickListener() {
            String individialvenderid = String.valueOf(currentenquiry.getVnderuser_id());
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), individialvenderid, Toast.LENGTH_SHORT).show();
                Intent callnow=new Intent(getContext(),AllTransactionVendor.class);
                callnow.putExtra("individialvenderid",individialvenderid);
                callnow.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(callnow);
                return;
            }
        });*/

        //this is the function for call to vendor
        holder.callvnder.setOnClickListener(new View.OnClickListener() {
            String myphone = String.valueOf(currentenquiry.getPhone());

            @Override
            public void onClick(View v) {
                Intent callnow = new Intent(getContext(), CalltoVendor.class);
                callnow.putExtra("phonenumber", myphone);
                callnow.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(callnow);
                // return;
            }
        });


        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.header);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.fitCenter();

        //It is a image loading framework
        Glide.with(getContext()).load(currentenquiry.getStore_image())
                .thumbnail(0.5f)
                .apply(requestOptions)
                .into(holder.vnderlogo);

        return convertView;
    }

    //search filter
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
            countryList = (ArrayList<Vendors>) results.values;
            notifyDataSetChanged();
            clear();
            for (int i = 0, l = countryList.size(); i < l; i++)
                add(countryList.get(i));
            notifyDataSetInvalidated();
        }
    }
}