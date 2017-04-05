package jiang.kaka.model.bean;

/**选择联系人
 * Created by 在云端 on 2016/10/28.
 */
public class PickContactInfo {
    private UserInfo user;
    private boolean isChecked;//是否被选择的标记

    public PickContactInfo(boolean isChecked, UserInfo user) {
        this.isChecked = isChecked;
        this.user = user;
    }

    public PickContactInfo() {
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public String toString() {
        return "PickContactInfo{" +
                "user=" + user +
                ", isChecked=" + isChecked +
                '}';
    }
}
