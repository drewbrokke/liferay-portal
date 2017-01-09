// This file was automatically generated from select.soy.
// Please don't edit this file by hand.

/**
 * @fileoverview Templates in namespace ddm.
 * @hassoydeltemplate {ddm.field}
 * @public
 */

if (typeof ddm == 'undefined') { var ddm = {}; }


ddm.__deltemplate_s2_2dbfb377 = function(opt_data, opt_ignored) {
  return '' + ddm.select(opt_data);
};
if (goog.DEBUG) {
  ddm.__deltemplate_s2_2dbfb377.soyTemplateName = 'ddm.__deltemplate_s2_2dbfb377';
}
soy.$$registerDelegateFn(soy.$$getDelTemplateId('ddm.field'), 'select', 0, ddm.__deltemplate_s2_2dbfb377);


ddm.select = function(opt_data, opt_ignored) {
  var output = '';
  var optionsSelected__soy5 = opt_data.value;
  output += '<div class="form-group' + soy.$$escapeHtmlAttribute(opt_data.visible ? '' : ' hide') + '" data-fieldname="' + soy.$$escapeHtmlAttribute(opt_data.name) + '"><div class="input-select-wrapper">' + ((opt_data.showLabel) ? ddm.select_label(opt_data) : '') + '<div class="form-builder-select-field input-group-container">' + ((! opt_data.readOnly) ? ddm.hidden_select(opt_data) : '') + ((! opt_data.multiple) ? '<a class="form-control select-field-trigger" dir="' + soy.$$escapeHtmlAttribute(opt_data.dir) + '" href="javascript:;" id="' + soy.$$escapeHtmlAttribute(opt_data.name) + '" name="' + soy.$$escapeHtmlAttribute(opt_data.name) + '">' + ((! opt_data.readOnly) ? (optionsSelected__soy5 && optionsSelected__soy5.value) ? '<span class="option-selected">' + soy.$$escapeHtml(optionsSelected__soy5.label) + '</span>' : '<span class="option-selected option-selected-placeholder">' + soy.$$escapeHtml(opt_data.strings.chooseAnOption) + '</span>' : '<span class="option-selected option-selected-placeholder">' + soy.$$escapeHtml(opt_data.strings.chooseAnOption) + '</span>') + '</a>' : '') + ((! opt_data.readOnly) ? '<div class="drop-chosen hide"><div class="search-chosen"><div class="select-search-container">' + ((opt_data.selectSearchIcon) ? '<a class="" href="javascript:;">' + soy.$$filterNoAutoescape(opt_data.selectSearchIcon) + '</a>' : '') + '</div><input autocomplete="off" class="drop-chosen-search" placeholder="Search" type="text"></div><ul class="results-chosen">' + ddm.select_options(opt_data) + '</ul></div>' : '') + ((opt_data.selectCaretDoubleIcon) ? '<a class="select-arrow-down-container" href="javascript:;">' + soy.$$filterNoAutoescape(opt_data.selectCaretDoubleIcon) + '</a>' : '') + '</div>' + ((opt_data.childElementsHTML) ? soy.$$filterNoAutoescape(opt_data.childElementsHTML) : '') + '</div></div>';
  return output;
};
if (goog.DEBUG) {
  ddm.select.soyTemplateName = 'ddm.select';
}


ddm.select_label = function(opt_data, opt_ignored) {
  return '<label class="control-label" for="' + soy.$$escapeHtmlAttribute(opt_data.name) + '">' + soy.$$escapeHtml(opt_data.label) + ((opt_data.required) ? '<span class="icon-asterisk text-warning"></span>' : '') + '</label>' + ((opt_data.tip) ? '<p class="liferay-ddm-form-field-tip">' + soy.$$escapeHtml(opt_data.tip) + '</p>' : '');
};
if (goog.DEBUG) {
  ddm.select_label.soyTemplateName = 'ddm.select_label';
}


ddm.hidden_select = function(opt_data, opt_ignored) {
  var output = '<select class="form-control hide" dir="' + soy.$$escapeHtmlAttribute(opt_data.dir) + '" id="' + soy.$$escapeHtmlAttribute(opt_data.name) + '" name="' + soy.$$escapeHtmlAttribute(opt_data.name) + '" ' + ((opt_data.multiple) ? 'multiple size="' + soy.$$escapeHtmlAttribute(opt_data.options.length) + '"' : '') + '>' + ((! opt_data.readOnly) ? '<option dir="' + soy.$$escapeHtmlAttribute(opt_data.dir) + '" disabled ' + ((! opt_data.value) ? 'selected' : '') + ' value="">' + soy.$$escapeHtml(opt_data.strings.chooseAnOption) + '</option>' : '');
  var optionList122 = opt_data.options;
  var optionListLen122 = optionList122.length;
  for (var optionIndex122 = 0; optionIndex122 < optionListLen122; optionIndex122++) {
    var optionData122 = optionList122[optionIndex122];
    var selectedValue__soy107 = '' + ((opt_data.value && opt_data.value.value) ? soy.$$escapeHtml(opt_data.value.value) : '');
    output += '<option dir="' + soy.$$escapeHtmlAttribute(opt_data.dir) + '" ' + ((selectedValue__soy107 == optionData122.value) ? 'selected' : '') + ' value="' + soy.$$escapeHtmlAttribute(optionData122.value) + '">' + soy.$$escapeHtml(optionData122.label) + '</option>';
  }
  output += '</select>';
  return output;
};
if (goog.DEBUG) {
  ddm.hidden_select.soyTemplateName = 'ddm.hidden_select';
}


ddm.select_options = function(opt_data, opt_ignored) {
  var output = '';
  var optionsSelected__soy126 = opt_data.value;
  var optionList142 = opt_data.options;
  var optionListLen142 = optionList142.length;
  for (var optionIndex142 = 0; optionIndex142 < optionListLen142; optionIndex142++) {
    var optionData142 = optionList142[optionIndex142];
    var selectedValue__soy127 = '' + ((optionsSelected__soy126 && optionsSelected__soy126.value) ? soy.$$escapeHtml(optionsSelected__soy126.value) : '');
    output += '<li class="' + ((selectedValue__soy127 == optionData142.value) ? 'option-selected' : '') + '" data-option-index="' + soy.$$escapeHtmlAttribute(optionIndex142) + '" data-option-value="' + soy.$$escapeHtmlAttribute(optionData142.value) + '">' + soy.$$escapeHtml(optionData142.label) + '</li>';
  }
  return output;
};
if (goog.DEBUG) {
  ddm.select_options.soyTemplateName = 'ddm.select_options';
}
