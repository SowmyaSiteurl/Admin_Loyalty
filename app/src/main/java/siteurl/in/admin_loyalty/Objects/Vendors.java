package siteurl.in.admin_loyalty.Objects;

/**
 * Created by siteurl on 10/11/17.
 */

public class Vendors {
    String vnderuser_id;
    String name;
    String email;
    String phone;
    String address,hash,store_image;

    public Vendors(String store_image, String vnderuser_id, String name, String email, String phone, String address) {

        this.store_image = store_image;
        this.vnderuser_id = vnderuser_id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;

    }
    public String getStore_image() {
        return store_image;
    }

    public String getHash() {
        return hash;
    }

    public String getVnderuser_id() {
        return vnderuser_id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "Vendors{" +
                "vnderuser_id='" + vnderuser_id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", hash='" + hash + '\'' +
                ", store_image='" + store_image + '\'' +
                '}';
    }
}
