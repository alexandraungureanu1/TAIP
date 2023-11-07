import React from 'react';
import FormField from './FormField';

const TextInput = ({ label, ...props }) => (
  <FormField label={label}>
    <input type="text" {...props} />
  </FormField>
);

export default TextInput;
