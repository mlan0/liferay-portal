/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.dynamic.data.mapping.form.evaluator.internal.function;

import com.liferay.dynamic.data.mapping.expression.DDMExpressionFunction;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionParameterAccessor;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionParameterAccessorAware;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.list.type.service.ListTypeEntryLocalServiceUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import org.osgi.service.component.annotations.Reference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Mateus Santana
 */
public class IsPicklistObjectFieldFunction
	implements DDMExpressionFunction.Function1<String, Boolean>,
			   DDMExpressionParameterAccessorAware {

	public static final String NAME = "IsPicklistObjectField";

	public IsPicklistObjectFieldFunction(JSONFactory jsonFactory) {
		this.jsonFactory = jsonFactory;
	}

	@Override
	public Boolean apply(String fieldValue) {
		String fieldValueName = fieldValue.replaceAll("\\[|\\]|\"", "");

		JSONArray objectFieldsJSONArray =
			_ddmExpressionParameterAccessor.getObjectFields();

		JSONObject jsonObject = _getJSONObject(
			fieldValueName, objectFieldsJSONArray);

		if (jsonObject != null) {
//			List<ListTypeEntry> listTypeEntries =
//				_listTypeEntryLocalService.getListTypeEntries(
//					(Integer)jsonObject.get("listTypeDefinitionId"));

			List<ListTypeEntry> listTypeEntries =
				ListTypeEntryLocalServiceUtil.getListTypeEntries(
					(Long) jsonObject.get("listTypeDefinitionId"));

			Map<String, String> keyValueOptions = new HashMap<>();

			for (ListTypeEntry listTypeEntry : listTypeEntries) {

				String listTypeEntryName = listTypeEntry.getName(
					listTypeEntry.getDefaultLanguageId());

				String listTypeEntryKey = listTypeEntry.getKey();

				keyValueOptions.put(listTypeEntryKey, listTypeEntryName);
			}

			return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void setDDMExpressionParameterAccessor(
		DDMExpressionParameterAccessor ddmExpressionParameterAccessor) {

		_ddmExpressionParameterAccessor = ddmExpressionParameterAccessor;
	}

	protected JSONFactory jsonFactory;

	private JSONObject _getJSONObject(
		String fieldValueName, JSONArray objectFieldsJSONArray) {

		for (int i = 0; i < objectFieldsJSONArray.length(); i++) {
			JSONObject jsonObject = objectFieldsJSONArray.getJSONObject(i);

			String jsonObjectName = jsonObject.getString("name");

			if (Objects.equals(jsonObjectName, fieldValueName)) {
				return jsonObject;
			}
		}

		return null;
	}

	private DDMExpressionParameterAccessor _ddmExpressionParameterAccessor;

	@Reference
	private ListTypeEntryLocalService _listTypeEntryLocalService;

}