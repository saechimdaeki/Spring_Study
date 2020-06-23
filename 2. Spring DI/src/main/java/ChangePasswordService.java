public class ChangePasswordService {
    private MemberDao memberDao;
    public void ChangePassword(String email,String oldPwd,String newPwd){
        Member member=memberDao.SelectByEmail(email);
        if(member==null)
            throw new MemberNotFoundException();
        member.changePassword(oldPwd,newPwd);
        memberDao.update(member);
    }
    public void setMemberDao(MemberDao memberDao){
        this.memberDao=memberDao;
    }
}
