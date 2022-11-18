export interface MultiSelectItem {
	label: string;
	value: string;
}
export interface ValidatableMultiSelectItem extends MultiSelectItem {
	errorMessage?: string;
}
export interface InputGroup {
	accountRoles: ValidatableMultiSelectItem[];
	emailAddresses: ValidatableMultiSelectItem[];
	id: string;
}
