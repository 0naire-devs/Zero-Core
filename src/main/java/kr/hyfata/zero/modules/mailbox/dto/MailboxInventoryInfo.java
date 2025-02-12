package kr.hyfata.zero.modules.mailbox.dto;

import java.util.ArrayList;

public class MailboxInventoryInfo {
    private ArrayList<Mailbox> mailboxes;
    private int currentPage = 0;
    private boolean shouldCancel = false;

    public ArrayList<Mailbox> getMailboxes() {
        return mailboxes;
    }

    public void setMailboxes(ArrayList<Mailbox> mailboxes) {
        this.mailboxes = mailboxes;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public boolean isShouldCancel() {
        return shouldCancel;
    }

    public void setShouldCancel(boolean shouldCancel) {
        this.shouldCancel = shouldCancel;
    }
}
