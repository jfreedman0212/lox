#![allow(dead_code)]

#[derive(Debug)]
pub enum VirtualMachineErrorKind {
    Runtime,
    Compilation,
}

#[derive(Debug)]
pub struct VirtualMachineError {
    pub kind: VirtualMachineErrorKind,
    pub message: String,
}
