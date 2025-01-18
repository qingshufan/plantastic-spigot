package fan.cropsprocess.com.type;

import fan.cropsprocess.com.data.disease.CropDisease;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CropDiseaseTypeHandler extends BaseTypeHandler<CropDisease> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, CropDisease parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.name());
    }

    @Override
    public CropDisease getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String name = rs.getString(columnName);
        if (name != null) {
            try {
                return CropDisease.valueOf(name);
            } catch (IllegalArgumentException e) {
                // 处理未知的疾病名称
                return null; // 或者抛出异常，或返回一个默认值
            }
        }
        return null;
    }

    @Override
    public CropDisease getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String name = rs.getString(columnIndex);
        if (name != null) {
            try {
                return CropDisease.valueOf(name);
            } catch (IllegalArgumentException e) {
                // 处理未知的疾病名称
                return null; // 或者抛出异常，或返回一个默认值
            }
        }
        return null;
    }

    @Override
    public CropDisease getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String name = cs.getString(columnIndex);
        if (name != null) {
            try {
                return CropDisease.valueOf(name);
            } catch (IllegalArgumentException e) {
                // 处理未知的疾病名称
                return null; // 或者抛出异常，或返回一个默认值
            }
        }
        return null;
    }
}
