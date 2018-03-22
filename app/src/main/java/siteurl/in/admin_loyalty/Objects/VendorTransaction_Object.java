package siteurl.in.admin_loyalty.Objects;

/**
 * Created by siteurl on 16/12/17.
 */

public class VendorTransaction_Object {

    String vendor_trans_id;
    String user_id;
    String vendor_id;
    String points_earned_id;
    String vendor_payment_rec_id;
    String opening_balance;
    String converted_amount_approved;
    String payment_amount;
    String closing_balance;
    String created_at;
    String updated_at;
    String status;

    public VendorTransaction_Object(String vendor_trans_id, String user_id,
                                    String vendor_id, String points_earned_id,
                                    String vendor_payment_rec_id, String opening_balance,
                                    String converted_amount_approved, String payment_amount,
                                    String closing_balance, String created_at,
                                    String updated_at, String status) {
        this.vendor_trans_id = vendor_trans_id;
        this.user_id = user_id;
        this.vendor_id = vendor_id;
        this.points_earned_id = points_earned_id;
        this.vendor_payment_rec_id = vendor_payment_rec_id;
        this.opening_balance = opening_balance;
        this.converted_amount_approved = converted_amount_approved;
        this.payment_amount = payment_amount;
        this.closing_balance = closing_balance;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.status = status;
        //   this.BuyerName = BuyerName;

    }

    public String getVendor_trans_id() {
        return vendor_trans_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getVendor_id() {
        return vendor_id;
    }

    public String getPoints_earned_id() {
        return points_earned_id;
    }

    public String getVendor_payment_rec_id() {
        return vendor_payment_rec_id;
    }

    public String getOpening_balance() {
        return opening_balance;
    }

    public String getConverted_amount_approved() {
        return converted_amount_approved;
    }

    public String getPayment_amount() {
        return payment_amount;
    }

    public String getClosing_balance() {
        return closing_balance;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public String getStatus() {
        return status;
    }

    public void setVendor_trans_id(String vendor_trans_id) {
        this.vendor_trans_id = vendor_trans_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setVendor_id(String vendor_id) {
        this.vendor_id = vendor_id;
    }

    public void setPoints_earned_id(String points_earned_id) {
        this.points_earned_id = points_earned_id;
    }

    public void setVendor_payment_rec_id(String vendor_payment_rec_id) {
        this.vendor_payment_rec_id = vendor_payment_rec_id;
    }

    public void setOpening_balance(String opening_balance) {
        this.opening_balance = opening_balance;
    }

    public void setConverted_amount_approved(String converted_amount_approved) {
        this.converted_amount_approved = converted_amount_approved;
    }

    public void setPayment_amount(String payment_amount) {
        this.payment_amount = payment_amount;
    }

    public void setClosing_balance(String closing_balance) {
        this.closing_balance = closing_balance;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "VendorTransaction_Object{" +
                "vendor_trans_id='" + vendor_trans_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", vendor_id='" + vendor_id + '\'' +
                ", points_earned_id='" + points_earned_id + '\'' +
                ", vendor_payment_rec_id='" + vendor_payment_rec_id + '\'' +
                ", opening_balance='" + opening_balance + '\'' +
                ", converted_amount_approved='" + converted_amount_approved + '\'' +
                ", payment_amount='" + payment_amount + '\'' +
                ", closing_balance='" + closing_balance + '\'' +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
