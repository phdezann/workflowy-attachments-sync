package org.phdezann.cn.model.workflowy;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Config {

    public enum ConfigKey {
        WORKFLOWY_SESSION_ID,
    }

    private Map<ConfigKey, String> entries;

    public String get(ConfigKey key) {
        return entries.get(key);
    }
}
