    package com.rotido.backend.model;

    import org.springframework.data.annotation.Id;
    import org.springframework.data.mongodb.core.mapping.Document;

    @Document(collection = "customers")
    public class Customer {

        @Id
        private String customerNumber;
        private String username;
        private String customerName;
        private String mail;
        private String phoneNumber;
        private String address;
        private String latitudeLongitude;

        // Getters
        public String getCustomerNumber() {
            return customerNumber;
        }

        public String getUsername() {
            return username;
        }

        public String getCustomerName() {
            return customerName;
        }

        public String getMail() {
            return mail;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public String getAddress() {
            return address;
        }

        public String getLatitudeLongitude() {
            return latitudeLongitude;
        }

        // ✅ Setters (important for JSON -> Object mapping)
        public void setCustomerNumber(String customerNumber) {
            this.customerNumber = customerNumber;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public void setMail(String mail) {
            this.mail = mail;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public void setLatitudeLongitude(String latitudeLongitude) {
            this.latitudeLongitude = latitudeLongitude;
        }
    }
