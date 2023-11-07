import React from 'react';

const withValidation = (WrappedComponent) => {
  return class extends React.Component {
    validate = (values) => {
      // validation logic
    };

    render() {
      return (
        <WrappedComponent validate={this.validate} {...this.props} />
      );
    }
  };
};

export default withValidation;
