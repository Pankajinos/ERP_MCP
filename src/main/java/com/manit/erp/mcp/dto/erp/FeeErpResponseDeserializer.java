package com.manit.erp.mcp.dto.erp;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom Jackson deserializer for FeeErpResponse.
 * Seamlessly handles:
 * 1. Root JSON array: [ { ... }, { ... } ]
 * 2. Root JSON object with "feeData": { "feeData": [ ... ] }
 * 3. Root JSON object with "data": { "data": [ ... ] }
 * 4. Nested status wrapper: { "status": "SUCCESS", "data": { "feeData": [ ... ] } }
 */
public class FeeErpResponseDeserializer extends JsonDeserializer<FeeErpResponse> {

    @Override
    public FeeErpResponse deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        if (node == null || node.isNull()) {
            return new FeeErpResponse(List.of(), List.of());
        }

        List<FeeErpResponse.FeeItem> items = new ArrayList<>();

        if (node.isArray()) {
            for (JsonNode itemNode : node) {
                FeeErpResponse.FeeItem item = p.getCodec().treeToValue(itemNode, FeeErpResponse.FeeItem.class);
                if (item != null) {
                    items.add(item);
                }
            }
            return new FeeErpResponse(items, items);
        }

        if (node.isObject()) {
            JsonNode targetArrayNode = null;

            if (node.has("feeData") && node.get("feeData").isArray()) {
                targetArrayNode = node.get("feeData");
            } else if (node.has("data")) {
                JsonNode dataNode = node.get("data");
                if (dataNode.isArray()) {
                    targetArrayNode = dataNode;
                } else if (dataNode.isObject() && dataNode.has("feeData") && dataNode.get("feeData").isArray()) {
                    targetArrayNode = dataNode.get("feeData");
                }
            }

            if (targetArrayNode != null && targetArrayNode.isArray()) {
                for (JsonNode itemNode : targetArrayNode) {
                    FeeErpResponse.FeeItem item = p.getCodec().treeToValue(itemNode, FeeErpResponse.FeeItem.class);
                    if (item != null) {
                        items.add(item);
                    }
                }
            }
            return new FeeErpResponse(items, items);
        }

        return new FeeErpResponse(List.of(), List.of());
    }
}
