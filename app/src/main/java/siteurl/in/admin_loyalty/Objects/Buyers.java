package siteurl.in.admin_loyalty.Objects;

/**
 * Created by siteurl on 20/11/17.
 */

public class Buyers {
    String vnderuser_id;
    String name;
    String email;
    String phone;
    String address,hash;

    public Buyers(String vnderuser_id, String name, String email, String phone, String address, String hash) {
        super();
        this.vnderuser_id = vnderuser_id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.hash = hash;
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
        return "Buyers{" +
                "vnderuser_id='" + vnderuser_id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", hash='" + hash + '\'' +
                '}';
    }
}
