package com.mimiton.redroid.base.processor;

import com.mimiton.redroid.base.ViewModel;

class DataBindProcessor extends AttributeProcessor {
 public DataBindProcessor (ViewModel contextViewModel, ViewModel targetViewModel) {
   super(contextViewModel, targetViewModel);
 }

 @Override
 public void process (String name, String value, String namespace) {
   targetViewModel.bindProperty(name, contextViewModel.getProperty(value));
 }
}
