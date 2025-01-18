package fan.cropsprocess.com.type;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.io.*;
import java.sql.*;
import java.util.Map;

public class MapTypeHandler extends BaseTypeHandler<Map<String, Boolean>> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Map<String, Boolean> parameter, JdbcType jdbcType) throws SQLException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(parameter);
            out.flush();
            ps.setBlob(i, new ByteArrayInputStream(bos.toByteArray()));
        } catch (IOException e) {
            throw new SQLException("Failed to serialize map", e);
        }
    }

    @Override
    public Map<String, Boolean> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return deserialize(rs.getBlob(columnName));
    }

    @Override
    public Map<String, Boolean> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return deserialize(rs.getBlob(columnIndex));
    }

    @Override
    public Map<String, Boolean> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return deserialize(cs.getBlob(columnIndex));
    }

    private Map<String, Boolean> deserialize(Blob blob) {
        if (blob == null) {
            return null;
        }
        try (ObjectInputStream in = new ObjectInputStream(blob.getBinaryStream())) {
            return (Map<String, Boolean>) in.readObject();
        } catch (IOException | ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Failed to deserialize map", e);
        }
    }
}
