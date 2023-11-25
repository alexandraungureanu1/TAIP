import React from 'react';
import FormField from './FormField';

const FileInput = ({ label, ...props }) => (
    <FormField label={label}>
      <input type="file" {...props} />
    </FormField>
  );

export default FileInput;
  