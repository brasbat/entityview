import React, {Component} from 'react';
import './App.css';
import axios from "axios";
import LoadingOverlay from 'react-loading-overlay';
import {Button, Modal} from 'react-bootstrap';

class App extends Component {

    constructor(props) {
        super(props);
        this.handleEntityChoosen.bind(this);
        this.state = {entity: "test", columns: ["a", "b", "c"]}
        this.tableRef = React.createRef();
    }

    handleEntityChoosen(name) {

        this.tableRef.current.updateShownEntity(name);
    }

    render() {
        return (
            <div className="App ">
                <nav className="navbar navbar-light bg-light">
                    <span className="navbar-brand mb-0 h1">Entity Viewer</span>
                </nav>
                <div className="container-fluid p-3">
                    <EntityChooser handler={this}/>
                </div>
                <div className="container-fluid p-3">

                    <EntityTable ref={this.tableRef}/>
                </div>
            </div>
        );
    }
}

class EditModal extends Component {
    constructor(props) {
        super(props);
        this.state = {name: this.props.name, data: this.props.column, show: this.props.show};
        // this.handleClose.bind(this);
    }

    update = function (data) {
        this.setState({name: data.name, data: data.selectedData, show: data.show})
    }
    handleClose = () => {
        this.setState({show: false});
    }

    render() {
        return (
            <Modal show={this.state.show} onHide={this.handleClose}>
                <Modal.Header closeButton>
                    <Modal.Title>Edit {this.state.name}</Modal.Title>
                </Modal.Header>
                <Modal.Body>You're about to edit<br/>{JSON.stringify(this.state.data)}</Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={this.handleClose}>
                        Cancel
                    </Button>
                    <Button variant="primary" onClick={this.handleClose}>
                        Save
                    </Button>
                </Modal.Footer>
            </Modal>
        )
    }
}

class EntityChooser extends Component {
    constructor(props) {
        super(props);
        this.state = {entities: []};
    }

    componentWillMount() {
        axios
            .get("http://localhost:8080/entity/repository/")
            .then(response => {

                this.setState({entities: response.data});
            })
    }

    render() {
        var self = this;
        return (
            <div className="dropdown">
                <button className="btn btn-default dropdown-toggle" type="button" id="entitiesDropdown"
                        data-toggle="dropdown"
                        aria-haspopup="true" aria-expanded="false">
                    Available Entities
                </button>
                <div className="dropdown-menu" aria-labelledby="entitiesDropdown">
                    {this.state.entities.map(function (d, idx) {
                        return (<a className="dropdown-item" id={d} href="#"
                                   onClick={() => self.props.handler.handleEntityChoosen(d)}>{d}</a>
                        )
                    })}
                </div>
            </div>
        )
    }
}

class EntityTable extends Component {
    constructor(props) {
        super(props);
        this.state = {isLoading: false};
        this.editRef = React.createRef();
    }

    updateShownEntity(name) {
        this.setState({isLoading: true, entity: name});
        axios
            .get("http://localhost:8080/entity/repository/data/" + name)
            .then(response => {

                this.setState({
                    entity: name,
                    columns: response.data.columns,
                    entityData: response.data.content,
                    enumValues: response.data.enumColumnToValuesMap,
                    idColumn: response.data.idColumn,
                    columnTypeMap: response.data.columnToColumnTypeMap,
                    isLoading: false
                })
            })
    }

    renderColumnValue(value, column) {
        let type = this.state.columnTypeMap[column]
        switch (type) {
            case "boolean":
                return (
                    <div className="custom-control custom-checkbox"><input type="checkbox"
                                                                           className="custom-control-input" checked={value}
                                                                           disabled /></div>
            );
            default:
            return value + "";
            }
            }

            render() {
                let body;
                if (this.state.columns == null) {
                body = (<label>Please choose an entity from dropdown first</label>)
            } else {
                body = (
                <div height="100%">
                <EditModal ref={this.editRef}/>

                <table className="table table-bordered table-hover table-sm">
                <tr>
                {this.state.columns.map((d) => <th>{d}</th>)}
                <th className="text-center fit">
                <span className="fa fa-tasks"></span>
                </th>
                </tr>
                <tbody>
                {this.state.entityData.map((row) =>
                    <tr>
                        {this.state.columns.map((columnName) =>
                            <td>{this.renderColumnValue(row[columnName], columnName)}</td>)}
                        <td className="text-center fit">
                            <div className="btn-group" role="group" aria-label="Edit section">
                                <button type="button" className="btn btn-outline-primary"
                                        onClick={() => this.editRef.current.update({
                                            selectedData: row,
                                            name: this.state.entity,
                                            show: true
                                        })}><span
                                    className="fa fa-pencil"></span></button>
                                <button type="button" className="btn btn-outline-danger"><span
                                    className="fa fa-trash"></span></button>
                            </div>
                        </td>
                    </tr>)}
                </tbody>
                </table>
                </div>
                )
            }

                return (
                <div>
                <LoadingOverlay active={this.state.isLoading}
                spinner
                text={'Loading data for ' + this.state.entity + ' ...'}
                fadeSpeed={50}
                >
                {body}
                </LoadingOverlay>
                </div>
                )
            }
            }

            export default App;
