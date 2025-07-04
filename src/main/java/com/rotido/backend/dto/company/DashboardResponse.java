package com.rotido.backend.dto.company;

public class DashboardResponse {
    private int totalMachines;
    private int machinesSubscribed;
    private int machinesSold;
    private int totalChapatisMade;
    private int subscriptionsEndingToday;
    private int subscriptionsEndingThisMonth;

    // Constructors
    public DashboardResponse() {}

    public DashboardResponse(int totalMachines, int machinesSubscribed, int machinesSold,
                             int totalChapatisMade, int subscriptionsEndingToday, int subscriptionsEndingThisMonth) {
        this.totalMachines = totalMachines;
        this.machinesSubscribed = machinesSubscribed;
        this.machinesSold = machinesSold;
        this.totalChapatisMade = totalChapatisMade;
        this.subscriptionsEndingToday = subscriptionsEndingToday;
        this.subscriptionsEndingThisMonth = subscriptionsEndingThisMonth;
    }

    // Getters and Setters
    public int getTotalMachines() {
        return totalMachines;
    }

    public void setTotalMachines(int totalMachines) {
        this.totalMachines = totalMachines;
    }

    public int getMachinesSubscribed() {
        return machinesSubscribed;
    }

    public void setMachinesSubscribed(int machinesSubscribed) {
        this.machinesSubscribed = machinesSubscribed;
    }

    public int getMachinesSold() {
        return machinesSold;
    }

    public void setMachinesSold(int machinesSold) {
        this.machinesSold = machinesSold;
    }

    public int getTotalChapatisMade() {
        return totalChapatisMade;
    }

    public void setTotalChapatisMade(int totalChapatisMade) {
        this.totalChapatisMade = totalChapatisMade;
    }

    public int getSubscriptionsEndingToday() {
        return subscriptionsEndingToday;
    }

    public void setSubscriptionsEndingToday(int subscriptionsEndingToday) {
        this.subscriptionsEndingToday = subscriptionsEndingToday;
    }

    public int getSubscriptionsEndingThisMonth() {
        return subscriptionsEndingThisMonth;
    }

    public void setSubscriptionsEndingThisMonth(int subscriptionsEndingThisMonth) {
        this.subscriptionsEndingThisMonth = subscriptionsEndingThisMonth;
    }
}
