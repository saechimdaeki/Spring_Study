package demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@RestController
public class MemberApi {
    private JdbcTemplate jdbcTemplate;
    private RowMapper<Member> memRowMapper =
            new RowMapper<Member>() {
                @Override
                public Member mapRow(ResultSet rs, int rowNum)
                        throws SQLException {
                    Member member = new Member(rs.getString("EMAIL"),
                            rs.getString("PASSWORD"),
                            rs.getString("NAME"),
                            rs.getTimestamp("REGDATE").toLocalDateTime());
                    member.setId(rs.getLong("ID"));
                    return member;
                }
            };
    @GetMapping("/members")
    public List<Member> selectAll() {
        List<Member> results = jdbcTemplate.query("select * from MEMBER",
                memRowMapper);
        return results;
    }
    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }
}
