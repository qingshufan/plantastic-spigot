package fan.cropsprocess.com.type;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UuidTypeHandler extends BaseTypeHandler<UUID> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, UUID parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.toString());
    }

    @Override
    public UUID getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String uuidStr = rs.getString(columnName);
        return uuidStr != null ? UUID.fromString(uuidStr) : null;
    }

    @Override
    public UUID getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String uuidStr = rs.getString(columnIndex);
        return uuidStr != null ? UUID.fromString(uuidStr) : null;
    }

    @Override
    public UUID getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String uuidStr = cs.getString(columnIndex);
        return uuidStr != null ? UUID.fromString(uuidStr) : null;
    }
}
