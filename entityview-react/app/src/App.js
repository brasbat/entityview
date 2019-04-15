import React, {Component} from 'react';
import './App.css';
import axios from "axios";
import LoadingOverlay from 'react-loading-overlay';
import {Button, Col, Form, Modal, Row} from 'react-bootstrap';
import Container from "react-bootstrap/Container";

const baseUrl = reactIsInDevelomentMode() ? "http://localhost:8080" : window.location.origin;

function reactIsInDevelomentMode() {
	return '_self' in React.createElement('div');
}

class App extends Component {

	constructor(props) {
		super(props);
		this.handleEntityChoosen.bind(this);
		this.state = {entity: "test", columns: ["a", "b", "c"]};
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
		this.state = {name: this.props.name, data: this.props.column, show: this.props.show, model: this.props.model};
		// this.handleClose.bind(this);
	}

	update = function(data) {
		this.setState({name: data.name, data: data.selectedData, enums: data.enumValues, columnTypes: data.columnTypes, show: data.show})
	}
	handleClose = () => {
		this.setState({show: false});
	}

	renderBody(value, name, type) {
		return (<Form.Group as={Row} controlId={name}>
			<Form.Label column>
				{name}
			</Form.Label>
			<Col>
				{this.renderInputForType(type, value, name)}
			</Col>
		</Form.Group>);
	}

	renderInputForType(type, value, name) {
		switch (type) {
			case "int":
			case "long":
			case "double":
			case "float":
				return (<Form.Control placeholder={name} defaultValue={value}/>);
			case "boolean":
				return (<Form.Check type="checkbox" value={value} label={name}/>);
			case "string":
				return (
						<textarea className="form-control rounded-5" rows="3" name={name}>
							{value}
                        </textarea>);
			case "date":
				return (<Form.Control type="datetime-local" value={value} placeholder={name}/>);
			case "enum":
				return (
						<Form.Control as="select">
							{console.log(this.state.enums[name])}
							{

								this.state.enums[name].map((d) => <option key={"enum" + d} id={"enum" + d}>{d}</option>)}
							}
						</Form.Control>);
			default:
				return (
						<textarea rows="3" cols={15} name={name}>
							{JSON.stringify(value)}
                        </textarea>);
		}
	}

	handleSubmit = (event) => {
		event.preventDefault();
		Object.keys(this.state.columnTypes).forEach((d, idx) =>
				console.log(d + " : " + event.target.elements[d].value)
	)
		;
		console.log(event.target.elements);
	}

	render() {
		if (this.state.data == null) {
			return (<Container/>);
		}
		return (
				<Modal show={this.state.show} onHide={this.handleClose}>
					<Form onSubmit={this.handleSubmit}>
						<Modal.Header closeButton>
							<Modal.Title>Edit {this.state.name}</Modal.Title>
						</Modal.Header>
						<Modal.Body>
							<div className="container-fluid p-3">
								{Object.keys(this.state.data).map((key) => this.renderBody(this.state.data[key], key, this.state.columnTypes[key]))}
							</div>
						</Modal.Body>
						<Modal.Footer>
							<Button variant="secondary" onClick={this.handleClose}>
								Cancel
							</Button>
							<Button variant="primary" type="submit" onClick={this.handleClose}>
								Save
							</Button>
						</Modal.Footer>
					</Form>
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
				.get(baseUrl + "/entity/api/repository/")
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
						{this.state.entities.map(function(d, idx) {
							// eslint-disable-next-line
							return (<a className="dropdown-item" key={d} id={d} href="#"
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
				.get(baseUrl + "/entity/api/repository/data/" + name)
				.then(response => {
					console.log(response);
					this.setState({
						entity: name,
						columns: response.data.columns,
						entityData: response.data.content,
						idColumn: response.data.idColumn,
						enumValues: response.data.enumColumnToValuesMap,
						columnTypeMap: response.data.columnToColumnTypeMap,
						isLoading: false
					});
					console.log(this.state)
				})
	}

	renderColumnValue(value, column) {
		let type = this.state.columnTypeMap[column];
		switch (type) {
			default:
				return value + "";
		}
	}

	render() {
		let body;
		const self = this;
		if (this.state.columns == null) {
			body = (<label>Please choose an entity from dropdown first</label>)
		} else {
			body = (
					<div height="100%">
						<EditModal key="editModal" ref={this.editRef}/>

						<table className="table table-bordered table-hover table-sm">
							<tbody  >
							<tr>
								{this.state.columns.map((d) => <th>{d}</th>)}
								<th className="text-center fit">
									<span className="fa fa-tasks"></span>
								</th>
							</tr>
							{this.state.entityData.map((row) =>
									<tr>
										{this.state.columns.map((columnName) =>
												<td>{this.renderColumnValue(row[columnName], columnName)}</td>)}
										<td className="text-center fit">
											<div className="btn-group" role="group" aria-label="Edit section">
												<button type="button" className="btn btn-outline-primary"
												        onClick={() => this.editRef.current.update({
													        selectedData: row,
													        name: self.state.entity,
													        show: true,
													        enumValues: self.state.enumValues,
													        columnTypes: self.state.columnTypeMap
												        })}>
													<span className="fa fa-pencil"></span></button>
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
